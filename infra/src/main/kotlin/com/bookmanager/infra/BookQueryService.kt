package com.bookmanager.infra

import com.bookmanager.entity.AuthorId
import com.bookmanager.entity.BookEntity
import com.bookmanager.jooq.Tables.BOOK
import com.bookmanager.jooq.Tables.BOOK_AUTHOR
import com.bookmanager.port.IBookQueryService
import com.bookmanager.vo.Authors
import com.bookmanager.vo.BookPublicationStatus
import com.bookmanager.vo.Price
import com.bookmanager.vo.Version
import org.jooq.DSLContext
import org.springframework.stereotype.Service

@Service
class BookQueryService(
    private val dsl: DSLContext
) : IBookQueryService {
    override fun findByAuthorId(id: AuthorId): List<BookEntity> =
        dsl.select()
            .from(BOOK)
            .innerJoin(BOOK_AUTHOR)
            .on(BOOK_AUTHOR.BOOK_ID.eq(BOOK.ID))
            .where(BOOK_AUTHOR.AUTHOR_ID.eq(id))
            .fetchInto(BOOK)
            .map { bookRecord ->
                val bookId = bookRecord.get(BOOK.ID)
                val authorIds = dsl.select(BOOK_AUTHOR.AUTHOR_ID)
                    .from(BOOK_AUTHOR)
                    .where(BOOK_AUTHOR.BOOK_ID.eq(bookId))
                    .orderBy(BOOK_AUTHOR.AUTHOR_ORDER.asc())
                    .fetchInto(Long::class.java)
                BookEntity(
                    id = bookId,
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
