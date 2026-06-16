package com.bookmanager

import com.bookmanager.command.RegisterBookCommand
import com.bookmanager.command.UpdateBookCommand
import com.bookmanager.dto.BookDto
import com.bookmanager.dto.BookDto.Companion.toDto
import com.bookmanager.entity.NewBookEntity
import com.bookmanager.port.IBookAppService
import com.bookmanager.port.IBookRepository
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
