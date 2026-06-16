package com.book_manager.repository

import com.book_manager.domain.entity.BookEntity
import com.book_manager.domain.entity.BookId
import com.book_manager.domain.entity.BookSchema
import com.book_manager.domain.entity.NewBookEntity
import com.book_manager.domain.port.IBookRepository
import com.book_manager.domain.vo.BookPublicationStatus
import com.book_manager.domain.vo.Price
import com.book_manager.domain.vo.Version
import com.book_manager.jooq.Tables.BOOK
import com.book_manager.jooq.Tables.BOOK_AUTHOR
import com.book_manager.jooq.enums.BookPublicationStatusCode
import com.book_manager.jooq.tables.records.BookAuthorRecord
import org.jooq.DSLContext
import org.jooq.impl.DSL.noCondition
import org.springframework.stereotype.Repository

@Repository
internal class BookRepository(
    private val dsl: DSLContext
) : IBookRepository {
    override fun findById(id: BookId): BookEntity {
        return findSingleById(id)
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
                book.authorIds.mapIndexed { index, authorId ->
                    BookAuthorRecord().apply {
                        set(BOOK_AUTHOR.BOOK_ID, book.id)
                        set(BOOK_AUTHOR.AUTHOR_ID, authorId)
                        set(BOOK_AUTHOR.AUTHOR_ORDER, index)
                    }
                }.let {
                    dsl.batchInsert(it)
                        .execute()
                }
                return findSingleById(book.id)
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
                    .returning()
                    .fetchSingleInto(Long::class.java)
                book.authorIds.mapIndexed { index, authorId ->
                    BookAuthorRecord().apply {
                        set(BOOK_AUTHOR.BOOK_ID, bookId)
                        set(BOOK_AUTHOR.AUTHOR_ID, authorId)
                        set(BOOK_AUTHOR.AUTHOR_ORDER, index)
                    }
                }.let {
                    dsl.batchInsert(it)
                        .execute()
                }
                return findSingleById(bookId)
            }
        }
    }

    private fun findSingleById(id: BookId): BookEntity =
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
                    authorIds = authorIds,
                    publicationStatus = BookPublicationStatus.valueOf(
                        bookRecord.get(BOOK.PUBLICATION_STATUS).name
                    ),
                    version = Version(bookRecord.get(BOOK.VERSION))
                )
            } ?: throw RuntimeException()
}
