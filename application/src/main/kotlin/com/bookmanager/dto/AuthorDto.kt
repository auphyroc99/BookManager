package com.bookmanager.dto

import com.bookmanager.entity.AuthorEntity
import java.time.LocalDate

data class AuthorDto(
    val id: Long,
    val name: String,
    val birthDate: LocalDate,
) {
    companion object {
        fun AuthorEntity.toDto(): AuthorDto =
            AuthorDto(
                id = this.id,
                name = this.name,
                birthDate = this.birthDate.date,
            )
    }
}
