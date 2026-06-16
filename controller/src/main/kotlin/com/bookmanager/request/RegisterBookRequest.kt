package com.bookmanager.request

import com.bookmanager.command.RegisterBookCommand

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
