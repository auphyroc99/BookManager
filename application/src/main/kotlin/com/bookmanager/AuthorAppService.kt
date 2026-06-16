package com.bookmanager

import com.bookmanager.command.RegisterAuthorCommand
import com.bookmanager.command.UpdateAuthorCommand
import com.bookmanager.dto.AuthorDto
import com.bookmanager.dto.AuthorDto.Companion.toDto
import com.bookmanager.entity.AuthorId
import com.bookmanager.entity.NewAuthorEntity
import com.bookmanager.port.IAuthorAppService
import com.bookmanager.port.IAuthorRepository
import com.bookmanager.vo.BirthDate
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
