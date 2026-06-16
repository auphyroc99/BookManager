package com.book_manager.controller.request

import com.book_manager.application.command.UpdateAuthorCommand
import java.time.LocalDate

data class UpdateAuthorRequest(
    val id: Long,
    val name: String,
    val birthDate: LocalDate,
) {
    fun toCommand(): UpdateAuthorCommand =
        UpdateAuthorCommand(
            id = id,
            name = name,
            birthDate = birthDate,
        )
}
