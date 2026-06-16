package com.book_manager.application

import com.book_manager.application.command.RegisterBookCommand
import com.book_manager.application.command.UpdateBookCommand
import com.book_manager.application.dto.BookDto
import com.book_manager.application.dto.BookDto.Companion.toDto
import com.book_manager.application.port.IBookAppService
import com.book_manager.domain.entity.NewBookEntity
import com.book_manager.domain.port.IBookRepository
import com.book_manager.domain.vo.BookPublicationStatus
import com.book_manager.domain.vo.Price
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
            authorIds = command.authorIds,
            publicationStatus = BookPublicationStatus.valueOf(command.publicationStatus),
        ).let {
            bookRepository.save(it).toDto()
        }

    @Transactional
    override fun updateBook(command: UpdateBookCommand): BookDto =
        bookRepository.findById(command.id)
            .updateTitle(command.title)
            .updatePrice(Price(command.price))
            .updateAuthorIds(command.authorIds)
            .let {
                bookRepository.save(it).toDto()
            }

    @Transactional
    override fun publishBook(id: Long): BookDto =
        bookRepository.findById(id)
            .publish()
            .let {
                bookRepository.save(it).toDto()
            }

    override fun fetchBookById(id: Long): BookDto? {
        return try {
            bookRepository.findById(id).toDto()
        } catch (e: NoSuchElementException) {
            null
        }
    }
}
