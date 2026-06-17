package com.bookmanager.entity

import com.bookmanager.vo.Authors
import com.bookmanager.vo.BookPublicationStatus
import com.bookmanager.vo.Price
import com.bookmanager.vo.Version

sealed interface BookSchema {
    val title: String
    val price: Price
    val authors: Authors
    val publicationStatus: BookPublicationStatus
}

typealias BookId = Long

data class BookEntity(
    val id: BookId,
    override val title: String,
    override val price: Price,
    override val authors: Authors,
    override val publicationStatus: BookPublicationStatus,
    val version: Version,
) : BookSchema {
    fun updateTitle(title: String) =
        if (title.isNotBlank()) {
            copy(title = title)
        } else {
            throw IllegalArgumentException("`title` of `book` must not be blank.")
        }

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
}

data class NewBookEntity(
    override val title: String,
    override val price: Price,
    override val authors: Authors,
    override val publicationStatus: BookPublicationStatus,
) : BookSchema
