package com.book_manager.controller.response

import com.book_manager.application.dto.BookDto

data class BookResponse(
    val id: Long,
    val title: String,
    val price: Int,
    val authorIds: List<Long>,
    val publicationStatus: String,
) {
    companion object {
        fun BookDto.toResponse() = BookResponse(
            id = this.id,
            title = this.title,
            price = this.price,
            authorIds = this.authorIds,
            publicationStatus = this.publicationStatus,
        )
    }
}
