package com.book_manager.domain.repository

import com.book_manager.domain.entity.BookSchema

interface IBookRepository {
    fun save(book: BookSchema)
}
