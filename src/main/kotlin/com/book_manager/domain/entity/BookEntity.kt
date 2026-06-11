package com.book_manager.domain.entity

import com.book_manager.domain.vo.BookPublicationStatus
import com.book_manager.domain.vo.Price
import com.book_manager.domain.vo.Version

sealed interface BookSchema {
    val title: String
    val price: Price
    val authorIds: List<AuthorId>
    val publicationStatus: BookPublicationStatus
}

typealias BookId = Long

data class BookEntity(
    val id: BookId,
    override val title: String,
    override val price: Price,
    override val authorIds: List<AuthorId>,
    override val publicationStatus: BookPublicationStatus,
    val version: Version,
) : BookSchema

data class NewBookEntity(
    override val title: String,
    override val price: Price,
    override val authorIds: List<AuthorId>,
    override val publicationStatus: BookPublicationStatus,
) : BookSchema
