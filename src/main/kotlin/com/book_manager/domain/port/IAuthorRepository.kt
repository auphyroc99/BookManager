package com.book_manager.domain.port

import com.book_manager.domain.entity.AuthorEntity
import com.book_manager.domain.entity.AuthorId
import com.book_manager.domain.entity.AuthorSchema

interface IAuthorRepository {
    fun findById(id: AuthorId): AuthorEntity
    fun save(author: AuthorSchema): AuthorEntity
}
