package com.bookmanager.command

data class UpdateBookCommand(
    val id: Long,
    val title: String,
    val price: Int,
    val authorIds: List<Long>,
)
