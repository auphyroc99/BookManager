package com.book_manager.application.port

import com.book_manager.application.command.RegisterBookCommand
import com.book_manager.application.command.UpdateBookCommand
import com.book_manager.application.dto.BookDto

interface IBookAppService {
    fun registerBook(command: RegisterBookCommand): BookDto
    fun updateBook(command: UpdateBookCommand): BookDto
    fun publishBook(id: Long): BookDto
    fun fetchBookById(id: Long): BookDto?
}
