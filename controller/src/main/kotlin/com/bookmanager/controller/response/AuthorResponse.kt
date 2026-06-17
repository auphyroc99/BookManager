package com.bookmanager.controller.response

import com.bookmanager.application.dto.AuthorDto
import java.time.LocalDate

data class AuthorResponse(
    val id: Long,
    val name: String,
    val birthDate: LocalDate,
) {
    companion object {
        fun AuthorDto.toResponse(): AuthorResponse = AuthorResponse(
            id = id,
            name = name,
            birthDate = birthDate,
        )
    }
}
