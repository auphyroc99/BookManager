package com.bookmanager

import com.bookmanager.command.RegisterBookCommand
import com.bookmanager.command.UpdateBookCommand
import com.bookmanager.dto.BookDto
import com.bookmanager.dto.BookDto.Companion.toDto
import com.bookmanager.entity.NewBookEntity
import com.bookmanager.exception.BookNotFoundException
import com.bookmanager.exception.OptimisticLockException
import com.bookmanager.exception.VersionConflictException
import com.bookmanager.port.IBookAppService
import com.bookmanager.port.IBookRepository
import com.bookmanager.vo.Authors
import com.bookmanager.vo.BookPublicationStatus
import com.bookmanager.vo.Price
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
internal class BookAppService(
    private val bookRepository: IBookRepository,
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
            ?.updateTitle(command.title)
            ?.updatePrice(Price(command.price))
            ?.updateAuthorIds(command.authorIds)
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
    override fun publishBook(id: Long): BookDto =
        bookRepository.findById(id)
            ?.publish()
            ?.let {
                runCatching {
                    bookRepository.save(it)
                }.getOrElse { ex ->
                    when (ex) {
                        is VersionConflictException -> {
                            throw OptimisticLockException(
                                "Book with id $id was modified by another transaction. Please retry the operation.",
                                ex
                            )
                        }

                        else -> throw ex
                    }
                }.toDto()
            } ?: throw BookNotFoundException(
            "Book with id $id not found"
        )

    override fun fetchBookById(id: Long): BookDto? =
        bookRepository.findById(id)?.toDto()
}
