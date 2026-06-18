package com.bookmanager.application

import com.bookmanager.application.command.RegisterAuthorCommand
import com.bookmanager.application.command.UpdateAuthorCommand
import com.bookmanager.application.dto.AuthorDto
import com.bookmanager.application.dto.AuthorDto.Companion.toDto
import com.bookmanager.domain.entity.AuthorId
import com.bookmanager.domain.entity.NewAuthorEntity
import com.bookmanager.application.exception.AuthorNotFoundException
import com.bookmanager.application.exception.OptimisticLockException
import com.bookmanager.infra.exception.VersionConflictException
import com.bookmanager.application.port.IAuthorAppService
import com.bookmanager.domain.entity.AuthorEntity
import com.bookmanager.domain.port.IAuthorRepository
import com.bookmanager.domain.vo.BirthDate
import com.bookmanager.domain.vo.Version
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
            ?.checkVersion(Version(command.version))
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

    companion object {
        private fun AuthorEntity.checkVersion(expectedVersion: Version): AuthorEntity =
            takeIf { version == expectedVersion }
                ?: throw OptimisticLockException(
                    "Expected version=$expectedVersion, actual version=$version"
                )
    }
}
