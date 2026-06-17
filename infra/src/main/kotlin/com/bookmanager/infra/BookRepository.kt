package com.bookmanager.infra

import com.bookmanager.entity.BookEntity
import com.bookmanager.entity.BookId
import com.bookmanager.entity.BookSchema
import com.bookmanager.entity.NewBookEntity
import com.bookmanager.jooq.Tables.BOOK
import com.bookmanager.jooq.Tables.BOOK_AUTHOR
import com.bookmanager.jooq.enums.BookPublicationStatusCode
import com.bookmanager.jooq.tables.records.BookAuthorRecord
import com.bookmanager.port.IBookRepository
import com.bookmanager.vo.Authors
import com.bookmanager.vo.BookPublicationStatus
import com.bookmanager.vo.Price
import com.bookmanager.vo.Version
import org.jooq.DSLContext
import org.jooq.impl.DSL.noCondition
import org.springframework.stereotype.Repository

@Repository
internal class BookRepository(
    private val dsl: DSLContext
) : IBookRepository {
    override fun findById(id: BookId): BookEntity? {
        return findOneById(id)
    }

    override fun save(book: BookSchema): BookEntity {
        when (book) {
            is BookEntity -> {
                dsl.update(BOOK)
                    .set(BOOK.TITLE, book.title)
                    .set(BOOK.PRICE, book.price.value)
                    .set(
                        BOOK.PUBLICATION_STATUS,
                        BookPublicationStatusCode.valueOf(book.publicationStatus.name)
                    )
                    .set(BOOK.VERSION, book.version.next().value)
                    .where(
                        noCondition()
                            .and(BOOK.ID.eq(book.id))
                            .and(BOOK.VERSION.eq(book.version.value))
                    )
                    .execute()
                    .let { countOfRows ->
                        if (countOfRows < 1) {
                            throw RuntimeException()
                        }
                    }
                dsl.deleteFrom(BOOK_AUTHOR)
                    .where(BOOK_AUTHOR.BOOK_ID.eq(book.id))
                    .execute()
                book.authors.authorIds.mapIndexed { index, authorId ->
                    BookAuthorRecord().apply {
                        set(BOOK_AUTHOR.BOOK_ID, book.id)
                        set(BOOK_AUTHOR.AUTHOR_ID, authorId)
                        set(BOOK_AUTHOR.AUTHOR_ORDER, index)
                    }
                }.let {
                    dsl.batchInsert(it)
                        .execute()
                }
                return findOneById(book.id) ?: throw RuntimeException(
                    "Unexpected error. Book with id ${book.id} not found after update."
                )
            }

            is NewBookEntity -> {
                val bookId: Long = dsl.insertInto(BOOK)
                    .set(BOOK.TITLE, book.title)
                    .set(BOOK.PRICE, book.price.value)
                    .set(
                        BOOK.PUBLICATION_STATUS,
                        BookPublicationStatusCode.valueOf(book.publicationStatus.name)
                    )
                    .set(BOOK.VERSION, 0)
                    .returning(BOOK.ID)
                    .fetchSingle()
                    .get(BOOK.ID)
                book.authors.authorIds.mapIndexed { index, authorId ->
                    BookAuthorRecord().apply {
                        set(BOOK_AUTHOR.BOOK_ID, bookId)
                        set(BOOK_AUTHOR.AUTHOR_ID, authorId)
                        set(BOOK_AUTHOR.AUTHOR_ORDER, index)
                    }
                }.let {
                    dsl.batchInsert(it)
                        .execute()
                }
                return findOneById(bookId) ?: throw RuntimeException(
                    "Unexpected error. Book with id $bookId not found after update."
                )
            }
        }
    }

    private fun findOneById(id: BookId): BookEntity? =
        dsl.select()
            .from(BOOK)
            .where(BOOK.ID.eq(id))
            .fetchOne()
            ?.let { bookRecord ->
                val authorIds = dsl.select(BOOK_AUTHOR.AUTHOR_ID)
                    .from(BOOK_AUTHOR)
                    .where(BOOK_AUTHOR.BOOK_ID.eq(id))
                    .orderBy(BOOK_AUTHOR.AUTHOR_ORDER.asc())
                    .fetchInto(Long::class.java)
                BookEntity(
                    id = bookRecord.get(BOOK.ID),
                    title = bookRecord.get(BOOK.TITLE),
                    price = Price(bookRecord.get(BOOK.PRICE)),
                    authors = Authors(authorIds),
                    publicationStatus = BookPublicationStatus.valueOf(
                        bookRecord.get(BOOK.PUBLICATION_STATUS).name
                    ),
                    version = Version(bookRecord.get(BOOK.VERSION))
                )
            }
}
