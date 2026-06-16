package com.book_manager.controller.request

import com.book_manager.application.command.UpdateBookCommand

data class UpdateBookRequest(
    val id: Long,
    val title: String,
    val price: Int,
    val authorIds: List<Long>,
) {
    fun toCommand(): UpdateBookCommand =
        UpdateBookCommand(
            id = this.id,
            title = this.title,
            price = this.price,
            authorIds = this.authorIds,
        )
}
