package com.book_manager.controller.request

import com.book_manager.application.command.RegisterBookCommand

data class RegisterBookRequest(
    val title: String,
    val price: Int,
    val authorIds: List<Long>,
    val publicationStatus: String,
) {
    fun toCommand(): RegisterBookCommand =
        RegisterBookCommand(
            title = this.title,
            price = this.price,
            authorIds = this.authorIds,
            publicationStatus = this.publicationStatus,
        )
}
