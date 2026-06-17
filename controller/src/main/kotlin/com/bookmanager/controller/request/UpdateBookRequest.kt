package com.bookmanager.controller.request

import com.bookmanager.application.command.UpdateBookCommand

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
