package com.bookmanager.domain.port

import com.bookmanager.domain.entity.BookEntity
import com.bookmanager.domain.entity.BookId
import com.bookmanager.domain.entity.BookSchema

interface IBookRepository {
    fun findById(id: BookId): BookEntity?
    fun save(book: BookSchema): BookEntity
}
