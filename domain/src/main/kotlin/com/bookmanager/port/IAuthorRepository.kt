package com.bookmanager.port

import com.bookmanager.entity.AuthorEntity
import com.bookmanager.entity.AuthorId
import com.bookmanager.entity.AuthorSchema

interface IAuthorRepository {
    fun findById(id: AuthorId): AuthorEntity?
    fun save(author: AuthorSchema): AuthorEntity
}
