package com.bookmanager.application.command

data class PublishBookCommand(
    val id: Long,
    val version: Int,
)
