package com.bookmanager.application.port

import com.bookmanager.application.command.RegisterAuthorCommand
import com.bookmanager.application.command.UpdateAuthorCommand
import com.bookmanager.application.dto.AuthorDto
import com.bookmanager.domain.entity.AuthorId

interface IAuthorAppService {
    fun registerAuthor(command: RegisterAuthorCommand): AuthorDto
    fun updateAuthor(command: UpdateAuthorCommand): AuthorDto
    fun fetchAuthorById(id: AuthorId): AuthorDto?
}
