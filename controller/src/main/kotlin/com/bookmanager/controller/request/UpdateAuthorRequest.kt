package com.bookmanager.controller.request

import com.bookmanager.application.command.UpdateAuthorCommand
import java.time.LocalDate

data class UpdateAuthorRequest(
    val id: Long,
    val name: String,
    val birthDate: LocalDate,
    val version: Int,
) {
    fun toCommand(): UpdateAuthorCommand =
        UpdateAuthorCommand(
            id = id,
            name = name,
            birthDate = birthDate,
            version = version,
        )
}
