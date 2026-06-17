package com.bookmanager.controller.request

import com.bookmanager.application.command.RegisterBookCommand

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
