package com.bookmanager.domain.port

import com.bookmanager.domain.entity.AuthorId
import com.bookmanager.domain.entity.BookEntity

interface IBookQueryService {
    fun findByAuthorId(id: AuthorId): List<BookEntity>
}
