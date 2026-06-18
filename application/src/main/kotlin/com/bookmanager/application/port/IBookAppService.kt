package com.bookmanager.application.port

import com.bookmanager.application.command.PublishBookCommand
import com.bookmanager.application.command.RegisterBookCommand
import com.bookmanager.application.command.UpdateBookCommand
import com.bookmanager.application.dto.BookDto
import com.bookmanager.application.param.SearchBooksParam

interface IBookAppService {
    fun registerBook(command: RegisterBookCommand): BookDto
    fun updateBook(command: UpdateBookCommand): BookDto
    fun publishBook(command: PublishBookCommand): BookDto
    fun fetchBookById(id: Long): BookDto?
    fun searchBooks(command: SearchBooksParam): List<BookDto>
}
