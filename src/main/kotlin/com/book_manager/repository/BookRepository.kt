package com.book_manager.repository

import com.book_manager.domain.entity.BookEntity
import com.book_manager.domain.entity.BookSchema
import com.book_manager.domain.entity.NewBookEntity
import com.book_manager.domain.repository.IBookRepository
import com.book_manager.jooq.Tables.BOOK
import com.book_manager.jooq.Tables.BOOK_AUTHOR
import com.book_manager.jooq.enums.BookPublicationStatus
import com.book_manager.jooq.tables.records.BookAuthorRecord
import com.book_manager.jooq.tables.records.BookRecord
import org.jooq.DSLContext
import org.jooq.impl.DSL.noCondition
import org.springframework.stereotype.Repository

@Repository
class BookRepository(
    private val dsl: DSLContext
): IBookRepository {

    override fun save(book: BookSchema) {

        when (book) {
            is BookEntity -> {
                dsl.update(BOOK)
                    .set(BOOK.TITLE, book.title)
                    .set(BOOK.PRICE, book.price.value)
                    .set(
                        BOOK.PUBLICATION_STATUS,
                        BookPublicationStatus.valueOf(book.publicationStatus.name)
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
            }

            is NewBookEntity -> {
                val bookRecord: BookRecord = dsl.insertInto(BOOK)
                    .set(BOOK.TITLE, book.title)
                    .set(BOOK.PRICE, book.price.value)
                    .set(
                        BOOK.PUBLICATION_STATUS,
                        BookPublicationStatus.valueOf(book.publicationStatus.name)
                    )
                    .set(BOOK.VERSION, 0)
                    .returning()
                    .fetchSingle()
                book.authorIds.mapIndexed { index, authorId ->
                    BookAuthorRecord().apply {
                        set(BOOK_AUTHOR.BOOK_ID, bookRecord.id)
                        set(BOOK_AUTHOR.AUTHOR_ID, authorId)
                        set(BOOK_AUTHOR.AUTHOR_ORDER, index)
                    }
                }.let {
                    dsl.batchInsert(it)
                        .execute()
                }
            }
        }
    }
}
