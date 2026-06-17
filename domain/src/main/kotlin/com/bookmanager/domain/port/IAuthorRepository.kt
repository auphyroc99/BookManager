package com.bookmanager.domain.port

import com.bookmanager.domain.entity.AuthorEntity
import com.bookmanager.domain.entity.AuthorId
import com.bookmanager.domain.entity.AuthorSchema

interface IAuthorRepository {
    fun findById(id: AuthorId): AuthorEntity?
    fun save(author: AuthorSchema): AuthorEntity
}
