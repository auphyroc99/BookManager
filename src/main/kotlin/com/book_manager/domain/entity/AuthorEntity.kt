package com.book_manager.domain.entity

import com.book_manager.domain.vo.BirthDate
import com.book_manager.domain.vo.Version

sealed interface AuthorSchema {
    val name: String
    val birthDate: BirthDate
}

typealias AuthorId = Long

class AuthorEntity(
    val id: AuthorId,
    override val name: String,
    override val birthDate: BirthDate,
    val version: Version,
): AuthorSchema

class NewAuthorEntity(
    override val name: String,
    override val birthDate: BirthDate,
): AuthorSchema
