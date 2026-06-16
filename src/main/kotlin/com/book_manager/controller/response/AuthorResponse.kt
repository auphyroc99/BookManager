package com.book_manager.controller.response

import com.book_manager.application.dto.AuthorDto
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
