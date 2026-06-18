package com.bookmanager.application

import com.bookmanager.application.command.PublishBookCommand
import com.bookmanager.application.command.RegisterBookCommand
import com.bookmanager.application.command.UpdateBookCommand
import com.bookmanager.application.dto.BookDto
import com.bookmanager.application.dto.BookDto.Companion.toDto
import com.bookmanager.application.exception.BookNotFoundException
import com.bookmanager.application.exception.OptimisticLockException
import com.bookmanager.application.param.SearchBooksParam
import com.bookmanager.application.port.IBookAppService
import com.bookmanager.domain.entity.BookEntity
import com.bookmanager.domain.entity.NewBookEntity
import com.bookmanager.infra.exception.VersionConflictException
import com.bookmanager.domain.port.IBookQueryService
import com.bookmanager.domain.port.IBookRepository
import com.bookmanager.domain.vo.Authors
import com.bookmanager.domain.vo.BookPublicationStatus
import com.bookmanager.domain.vo.Price
import com.bookmanager.domain.vo.Version
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
internal class BookAppService(
    private val bookRepository: IBookRepository,
    private val bookQueryService: IBookQueryService,
) : IBookAppService {
    @Transactional
    override fun registerBook(command: RegisterBookCommand): BookDto =
        NewBookEntity(
            title = command.title,
            price = Price(command.price),
            authors = Authors(command.authorIds),
            publicationStatus = BookPublicationStatus.valueOf(command.publicationStatus),
        ).let {
            bookRepository.save(it).toDto()
        }

    @Transactional
    override fun updateBook(command: UpdateBookCommand): BookDto =
        bookRepository.findById(command.id)
            ?.checkVersion(Version(command.version))
            ?.updateTitle(command.title)
            ?.updatePrice(Price(command.price))
            ?.updateAuthorIds(Authors(command.authorIds))
            ?.let {
                runCatching {
                    bookRepository.save(it)
                }.getOrElse { ex ->
                    when (ex) {
                        is VersionConflictException -> {
                            throw OptimisticLockException(
                                "Book with id ${command.id} was modified by another transaction. Please retry the operation.",
                                ex
                            )
                        }

                        else -> throw ex
                    }
                }.toDto()
            } ?: throw BookNotFoundException(
            "Book with id ${command.id} not found"
        )

    @Transactional
    override fun publishBook(command: PublishBookCommand): BookDto =
        bookRepository.findById(command.id)
            ?.checkVersion(Version(command.version))
            ?.publish()
            ?.let {
                runCatching {
                    bookRepository.save(it)
                }.getOrElse { ex ->
                    when (ex) {
                        is VersionConflictException -> {
                            throw OptimisticLockException(
                                "Book with id ${command.id} was modified by another transaction. Please retry the operation.",
                                ex
                            )
                        }

                        else -> throw ex
                    }
                }.toDto()
            } ?: throw BookNotFoundException(
            "Book with id ${command.id} not found"
        )

    override fun fetchBookById(id: Long): BookDto? =
        bookRepository.findById(id)?.toDto()

    override fun searchBooks(command: SearchBooksParam): List<BookDto> =
        bookQueryService.findByAuthorId(command.authorId).map { it.toDto() }

    companion object {
        private fun BookEntity.checkVersion(expectedVersion: Version): BookEntity =
            takeIf { version == expectedVersion }
                ?: throw OptimisticLockException(
                    "Expected version=$expectedVersion, actual version=$version"
                )
    }
}
