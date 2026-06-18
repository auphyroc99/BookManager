package com.bookmanager.controller.request

import com.bookmanager.application.command.PublishBookCommand

data class PublishBookRequest(
    val id: Long,
    val version: Int,
) {
    fun toCommand(): PublishBookCommand =
        PublishBookCommand(
            id = this.id,
            version = this.version,
        )
}
