package com.bookmanager

import com.bookmanager.port.IBookAppService
import com.bookmanager.request.RegisterBookRequest
import com.bookmanager.request.UpdateBookRequest
import com.bookmanager.response.BookResponse
import com.bookmanager.response.BookResponse.Companion.toResponse
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
