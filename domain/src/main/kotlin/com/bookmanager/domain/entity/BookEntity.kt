package com.bookmanager.domain.entity

import com.bookmanager.domain.vo.Authors
import com.bookmanager.domain.vo.BookPublicationStatus
import com.bookmanager.domain.vo.Price
import com.bookmanager.domain.vo.Version

sealed interface BookSchema {
    val title: String
    val price: Price
    val authors: Authors
    val publicationStatus: BookPublicationStatus
}

typealias BookId = Long

@ConsistentCopyVisibility
data class BookEntity private constructor(
    val id: BookId,
    override val title: String,
    override val price: Price,
    override val authors: Authors,
    override val publicationStatus: BookPublicationStatus,
    val version: Version,
) : BookSchema {
    init {
        require(title.isNotBlank()) {
            "`title` of `book` must not be blank."
        }
    }

    fun updateTitle(title: String) =
        copy(title = title)

    fun updatePrice(price: Price) =
        copy(price = price)

    fun updateAuthorIds(authors: Authors) =
        copy(authors = authors)

    fun publish() =
        if (publicationStatus == BookPublicationStatus.NOT_PUBLISHED) {
            copy(publicationStatus = BookPublicationStatus.PUBLISHED)
        } else {
            throw IllegalArgumentException("Only not published books can be published.")
        }

    companion object {
        operator fun invoke(
            id: BookId,
            title: String,
            price: Price,
            authors: Authors,
            publicationStatus: BookPublicationStatus,
            version: Version,
        ): BookEntity =
            BookEntity(
                id = id,
                title = title,
                price = price,
                authors = authors,
                publicationStatus = publicationStatus,
                version = version,
            )
    }
}

@ConsistentCopyVisibility
data class NewBookEntity private constructor(
    override val title: String,
    override val price: Price,
    override val authors: Authors,
    override val publicationStatus: BookPublicationStatus,
) : BookSchema {
    init {
        require(title.isNotBlank()) {
            "`title` of `book` must not be blank."
        }
    }

    companion object {
        operator fun invoke(
            title: String,
            price: Price,
            authors: Authors,
            publicationStatus: BookPublicationStatus,
        ): NewBookEntity =
            NewBookEntity(
                title = title,
                price = price,
                authors = authors,
                publicationStatus = publicationStatus,
            )
    }
}
