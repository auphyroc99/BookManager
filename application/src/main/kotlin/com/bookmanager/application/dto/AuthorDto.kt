package com.bookmanager.application.dto

import com.bookmanager.domain.entity.AuthorEntity
import java.time.LocalDate

data class AuthorDto(
    val id: Long,
    val name: String,
    val birthDate: LocalDate,
    val version: Int,
) {
    companion object {
        fun AuthorEntity.toDto(): AuthorDto =
            AuthorDto(
                id = this.id,
                name = this.name,
                birthDate = this.birthDate.date,
                version = this.version.value,
            )
    }
}
