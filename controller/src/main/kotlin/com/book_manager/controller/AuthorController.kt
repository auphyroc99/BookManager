package com.book_manager.controller

import com.book_manager.application.port.IAuthorAppService
import com.book_manager.controller.request.RegisterAuthorRequest
import com.book_manager.controller.request.UpdateAuthorRequest
import com.book_manager.controller.response.AuthorResponse
import com.book_manager.controller.response.AuthorResponse.Companion.toResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/authors")
@RestController
internal class AuthorController(
    private val authorAppService: IAuthorAppService
) {
    @PostMapping
    fun registerAuthor(@RequestBody request: RegisterAuthorRequest): AuthorResponse =
        authorAppService
            .registerAuthor(request.toCommand())
            .toResponse()

    @PutMapping
    fun updateAuthor(@RequestBody request: UpdateAuthorRequest): AuthorResponse =
        authorAppService
            .updateAuthor(request.toCommand())
            .toResponse()

    @GetMapping("/{id}")
    fun fetchAuthor(@PathVariable id: Long): AuthorResponse =
        authorAppService.fetchAuthorById(id)
            ?.toResponse()
            ?: throw IllegalArgumentException("Unknown author")
}