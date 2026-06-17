package com.bookmanager.port

import com.bookmanager.entity.AuthorId
import com.bookmanager.entity.BookEntity

interface IBookQueryService {
    fun findByAuthorId(id: AuthorId): List<BookEntity>
}
