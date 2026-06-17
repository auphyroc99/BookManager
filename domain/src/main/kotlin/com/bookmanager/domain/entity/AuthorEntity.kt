package com.bookmanager.domain.entity

import com.bookmanager.domain.vo.BirthDate
import com.bookmanager.domain.vo.Version

sealed interface AuthorSchema {
    val name: String
    val birthDate: BirthDate
}

typealias AuthorId = Long

@ConsistentCopyVisibility
data class AuthorEntity private constructor(
    val id: AuthorId,
    override val name: String,
    override val birthDate: BirthDate,
    val version: Version,
) : AuthorSchema {
    init {
        require(name.isNotBlank()) {
            "`name` of `author` must not be blank."
        }
    }

    fun updateName(name: String): AuthorEntity =
        copy(name = name)

    fun updateBirthDate(birthDate: BirthDate): AuthorEntity =
        copy(birthDate = birthDate)

    companion object {
        operator fun invoke(
            id: AuthorId,
            name: String,
            birthDate: BirthDate,
            version: Version,
        ): AuthorEntity =
            AuthorEntity(
                id = id,
                name = name,
                birthDate = birthDate,
                version = version,
            )
    }
}

@ConsistentCopyVisibility
data class NewAuthorEntity private constructor(
    override val name: String,
    override val birthDate: BirthDate,
) : AuthorSchema {
    init {
        require(name.isNotBlank()) {
            "`name` of `author` must not be blank."
        }
    }

    companion object {
        operator fun invoke(
            name: String,
            birthDate: BirthDate,
        ): NewAuthorEntity =
            NewAuthorEntity(
                name = name,
                birthDate = birthDate,
            )
    }
}
