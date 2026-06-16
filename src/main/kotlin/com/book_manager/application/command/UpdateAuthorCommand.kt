package com.book_manager.application.command

import java.time.LocalDate

data class UpdateAuthorCommand(
    val id: Long,
    val name: String,
    val birthDate: LocalDate,
)
