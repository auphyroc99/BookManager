package com.book_manager.domain.repository

import com.book_manager.domain.entity.AuthorSchema

interface IAuthorRepository {
    fun save(author: AuthorSchema)
}
