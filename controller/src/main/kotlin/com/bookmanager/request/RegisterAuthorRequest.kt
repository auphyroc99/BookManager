package com.bookmanager.request

import com.bookmanager.command.RegisterAuthorCommand
import java.time.LocalDate

data class RegisterAuthorRequest(
    val name: String,
    val birthDate: LocalDate,
) {
    fun toCommand(): RegisterAuthorCommand =
        RegisterAuthorCommand(
            name = this.name,
            birthDate = this.birthDate,
        )
}
