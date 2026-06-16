package com.book_manager.domain.port

import com.book_manager.domain.entity.BookEntity
import com.book_manager.domain.entity.BookId
import com.book_manager.domain.entity.BookSchema

interface IBookRepository {
    fun findById(id: BookId): BookEntity
    fun save(book: BookSchema): BookEntity
}
