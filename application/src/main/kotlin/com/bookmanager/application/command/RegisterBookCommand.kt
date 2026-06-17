package com.bookmanager.application.command

data class RegisterBookCommand(
    val title: String,
    val price: Int,
    val authorIds: List<Long>,
    val publicationStatus: String,
)
