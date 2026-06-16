package com.book_manager.domain.entity

import com.book_manager.domain.vo.BirthDate
import com.book_manager.domain.vo.Version

sealed interface AuthorSchema {
    val name: String
    val birthDate: BirthDate
}

typealias AuthorId = Long

data class AuthorEntity(
    val id: AuthorId,
    override val name: String,
    override val birthDate: BirthDate,
    val version: Version,
) : AuthorSchema {
    fun updateName(name: String): AuthorEntity =
        if (name.isNotBlank()) {
            copy(name = name)
        } else {
            throw IllegalArgumentException("`name` of `author` must not be blank.")
        }

    fun updateBirthDate(birthDate: BirthDate): AuthorEntity =
        copy(birthDate = birthDate)
}

data class NewAuthorEntity(
    override val name: String,
    override val birthDate: BirthDate,
) : AuthorSchema
