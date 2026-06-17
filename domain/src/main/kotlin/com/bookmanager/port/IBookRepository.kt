package com.bookmanager.port

import com.bookmanager.entity.BookEntity
import com.bookmanager.entity.BookId
import com.bookmanager.entity.BookSchema

interface IBookRepository {
    fun findById(id: BookId): BookEntity?
    fun save(book: BookSchema): BookEntity
}
