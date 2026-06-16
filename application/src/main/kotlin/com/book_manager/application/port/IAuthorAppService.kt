package com.book_manager.application.port

import com.book_manager.application.command.RegisterAuthorCommand
import com.book_manager.application.command.UpdateAuthorCommand
import com.book_manager.application.dto.AuthorDto
import com.book_manager.domain.entity.AuthorId

interface IAuthorAppService {
    fun registerAuthor(command: RegisterAuthorCommand): AuthorDto
    fun updateAuthor(command: UpdateAuthorCommand): AuthorDto
    fun fetchAuthorById(id: AuthorId): AuthorDto?
}
