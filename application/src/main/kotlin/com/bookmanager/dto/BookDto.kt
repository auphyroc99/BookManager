package com.bookmanager.dto

import com.bookmanager.entity.BookEntity

data class BookDto(
    val id: Long,
    val title: String,
    val price: Int,
    val authorIds: List<Long>,
    val publicationStatus: String,
) {
    companion object {
        fun BookEntity.toDto(): BookDto =
            BookDto(
                id = this.id,
                title = this.title,
                price = this.price.value,
                authorIds = this.authorIds,
                publicationStatus = this.publicationStatus.name,
            )
    }
}
