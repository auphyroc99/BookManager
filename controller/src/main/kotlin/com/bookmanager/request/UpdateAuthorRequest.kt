package com.bookmanager.request

import com.bookmanager.command.UpdateAuthorCommand
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
