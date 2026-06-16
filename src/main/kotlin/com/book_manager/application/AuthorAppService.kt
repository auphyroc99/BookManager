package com.book_manager.application

import com.book_manager.application.command.RegisterAuthorCommand
import com.book_manager.application.command.UpdateAuthorCommand
import com.book_manager.application.dto.AuthorDto
import com.book_manager.application.dto.AuthorDto.Companion.toDto
import com.book_manager.application.port.IAuthorAppService
import com.book_manager.domain.entity.AuthorId
import com.book_manager.domain.entity.NewAuthorEntity
import com.book_manager.domain.port.IAuthorRepository
import com.book_manager.domain.vo.BirthDate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
internal class AuthorAppService(
    private val authorRepository: IAuthorRepository
) : IAuthorAppService {
    @Transactional
    override fun registerAuthor(command: RegisterAuthorCommand): AuthorDto =
        NewAuthorEntity(
            name = command.name,
            birthDate = BirthDate(command.birthDate),
        ).let {
            authorRepository.save(it).toDto()
        }

    @Transactional
    override fun updateAuthor(command: UpdateAuthorCommand): AuthorDto =
        authorRepository.findById(command.id)
            .updateName(command.name)
            .updateBirthDate(BirthDate(command.birthDate))
            .let {
                authorRepository.save(it).toDto()
            }

    override fun fetchAuthorById(id: AuthorId): AuthorDto? {
        return try {
            authorRepository.findById(id).toDto()
        } catch (e: NoSuchElementException) {
            null
        }
    }
}
