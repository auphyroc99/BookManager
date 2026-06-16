package com.bookmanager.port

import com.bookmanager.command.RegisterBookCommand
import com.bookmanager.command.UpdateBookCommand
import com.bookmanager.dto.BookDto

interface IBookAppService {
    fun registerBook(command: RegisterBookCommand): BookDto
    fun updateBook(command: UpdateBookCommand): BookDto
    fun publishBook(id: Long): BookDto
    fun fetchBookById(id: Long): BookDto?
}
