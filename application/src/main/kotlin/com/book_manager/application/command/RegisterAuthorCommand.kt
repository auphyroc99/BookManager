package com.book_manager.application.command

import java.time.LocalDate

data class RegisterAuthorCommand(
    val name: String,
    val birthDate: LocalDate,
)
