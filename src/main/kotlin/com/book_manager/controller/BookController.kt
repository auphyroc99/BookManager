package com.book_manager.controller

import com.book_manager.application.port.IBookAppService
import com.book_manager.controller.request.RegisterBookRequest
import com.book_manager.controller.request.UpdateBookRequest
import com.book_manager.controller.response.BookResponse
import com.book_manager.controller.response.BookResponse.Companion.toResponse
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
    fun registerBook(@RequestBody request: RegisterBookRequest): BookResponse =
        bookAppService
            .registerBook(request.toCommand())
            .toResponse()

    @PutMapping
    fun updateBook(@RequestBody request: UpdateBookRequest): BookResponse =
        bookAppService
            .updateBook(request.toCommand())
            .toResponse()

    @PutMapping("/publish/{id}")
    fun publishBook(@PathVariable id: Long): BookResponse =
        bookAppService
            .publishBook(id)
            .toResponse()

    @GetMapping("/{id}")
    fun fetchBook(@PathVariable id: Long): BookResponse =
        bookAppService
            .fetchBookById(id)
            ?.toResponse()
            ?: throw IllegalArgumentException("Book with id $id not found.")
}
