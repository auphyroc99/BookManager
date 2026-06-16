package com.bookmanager.port

import com.bookmanager.command.RegisterAuthorCommand
import com.bookmanager.command.UpdateAuthorCommand
import com.bookmanager.dto.AuthorDto
import com.bookmanager.entity.AuthorId

interface IAuthorAppService {
    fun registerAuthor(command: RegisterAuthorCommand): AuthorDto
    fun updateAuthor(command: UpdateAuthorCommand): AuthorDto
    fun fetchAuthorById(id: AuthorId): AuthorDto?
}
