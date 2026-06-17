package com.bookmanager.application.command

import java.time.LocalDate

data class RegisterAuthorCommand(
    val name: String,
    val birthDate: LocalDate,
)
