package com.bookmanager.controller

import com.bookmanager.application.exception.BookNotFoundException
import com.bookmanager.application.exception.OptimisticLockException
import com.bookmanager.application.port.IBookAppService
import com.bookmanager.controller.exception.BadRequestException
import com.bookmanager.controller.exception.ConflictException
import com.bookmanager.controller.exception.NotFoundException
import com.bookmanager.controller.request.PublishBookRequest
import com.bookmanager.controller.request.RegisterBookRequest
import com.bookmanager.controller.request.SearchBooksRequest
import com.bookmanager.controller.request.UpdateBookRequest
import com.bookmanager.controller.response.BookResponse
import com.bookmanager.controller.response.BookResponse.Companion.toResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/books")
@RestController
internal class BookController(
    private val bookAppService: IBookAppService,
) {
    @PostMapping
    fun registerBook(@RequestBody request: RegisterBookRequest): ResponseEntity<BookResponse> =
        runCatching {
            bookAppService
                .registerBook(request.toCommand())
        }.fold(
            onSuccess = { dto ->
                dto.toResponse()
                    .let {
                        ResponseEntity
                            .status(HttpStatus.CREATED)
                            .body(it)
                    }
            },
            onFailure = { ex ->
                when (ex) {
                    is IllegalArgumentException ->
                        throw BadRequestException(ex.message ?: "Invalid request", ex)

                    else ->
                        throw ex
                }
            }
        )

    @PutMapping
    fun updateBook(@RequestBody request: UpdateBookRequest): ResponseEntity<BookResponse> =
        runCatching {
            bookAppService
                .updateBook(request.toCommand())
        }.fold(
            onSuccess = { dto ->
                dto.toResponse()
                    .let {
                        ResponseEntity
                            .ok(it)
                    }
            },
            onFailure = { ex ->
                when (ex) {
                    is IllegalArgumentException ->
                        throw BadRequestException(ex.message ?: "Invalid request", ex)

                    is BookNotFoundException ->
                        throw NotFoundException(ex.message ?: "Book not found", ex)

                    is OptimisticLockException ->
                        throw ConflictException(ex.message ?: "Conflict occurred while updating the book", ex)

                    else ->
                        throw ex
                }
            }
        )

    @PostMapping("/publish")
    fun publishBook(@RequestBody request: PublishBookRequest): ResponseEntity<BookResponse> =
        runCatching {
            bookAppService
                .publishBook(request.toCommand())
        }.fold(
            onSuccess = { dto ->
                dto.toResponse()
                    .let {
                        ResponseEntity
                            .ok(it)
                    }
            },
            onFailure = { ex ->
                when (ex) {
                    is IllegalArgumentException ->
                        throw BadRequestException(ex.message ?: "Invalid request", ex)

                    is BookNotFoundException ->
                        throw NotFoundException(ex.message ?: "Book not found", ex)

                    is OptimisticLockException ->
                        throw ConflictException(ex.message ?: "Conflict occurred while updating the book", ex)

                    else ->
                        throw ex
                }
            }
        )

    @GetMapping("/{id}")
    fun fetchBook(@PathVariable id: Long): ResponseEntity<BookResponse> =
        bookAppService
            .fetchBookById(id)
            ?.toResponse()
            ?.let {
                ResponseEntity
                    .ok(it)
            } ?: throw NotFoundException("Book with id $id not found.")

    @PostMapping("/search")
    fun searchBooks(@RequestBody request: SearchBooksRequest): ResponseEntity<List<BookResponse>> =
        bookAppService
            .searchBooks(request.toParam())
            .map { it.toResponse() }
            .let {
                ResponseEntity
                    .ok(it)
            }
}
