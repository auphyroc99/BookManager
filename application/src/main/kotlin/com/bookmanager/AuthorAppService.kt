package com.bookmanager

import com.bookmanager.command.RegisterAuthorCommand
import com.bookmanager.command.UpdateAuthorCommand
import com.bookmanager.dto.AuthorDto
import com.bookmanager.dto.AuthorDto.Companion.toDto
import com.bookmanager.entity.AuthorId
import com.bookmanager.entity.NewAuthorEntity
import com.bookmanager.exception.AuthorNotFoundException
import com.bookmanager.exception.OptimisticLockException
import com.bookmanager.exception.VersionConflictException
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
            ?.updateName(command.name)
            ?.updateBirthDate(BirthDate(command.birthDate))
            ?.let {
                runCatching {
                    authorRepository.save(it)
                }.getOrElse { ex ->
                    when (ex) {
                        is VersionConflictException -> {
                            throw OptimisticLockException(
                                "Author with id ${command.id} was modified by another transaction. Please retry the operation.",
                                ex
                            )
                        }

                        else -> throw ex
                    }
                }.toDto()
            } ?: throw AuthorNotFoundException(
            "Author with id ${command.id} not found"
        )

    override fun fetchAuthorById(id: AuthorId): AuthorDto? =
        authorRepository.findById(id)?.toDto()
}
