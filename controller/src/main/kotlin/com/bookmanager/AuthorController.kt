package com.bookmanager

import com.bookmanager.exception.AuthorNotFoundException
import com.bookmanager.exception.BadRequestException
import com.bookmanager.exception.ConflictException
import com.bookmanager.exception.NotFoundException
import com.bookmanager.exception.OptimisticLockException
import com.bookmanager.port.IAuthorAppService
import com.bookmanager.request.RegisterAuthorRequest
import com.bookmanager.request.UpdateAuthorRequest
import com.bookmanager.response.AuthorResponse
import com.bookmanager.response.AuthorResponse.Companion.toResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
    fun registerAuthor(@RequestBody request: RegisterAuthorRequest): ResponseEntity<AuthorResponse> =
        runCatching {
            authorAppService
                .registerAuthor(request.toCommand())
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
    fun updateAuthor(@RequestBody request: UpdateAuthorRequest): ResponseEntity<AuthorResponse> =
        runCatching {
            authorAppService
                .updateAuthor(request.toCommand())
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

                    is AuthorNotFoundException ->
                        throw NotFoundException(ex.message ?: "Author not found", ex)

                    is OptimisticLockException ->
                        throw ConflictException(ex.message ?: "Conflict occurred while updating the author", ex)

                    else ->
                        throw ex
                }
            }
        )

    @GetMapping("/{id}")
    fun fetchAuthor(@PathVariable id: Long): ResponseEntity<AuthorResponse> =
        authorAppService.fetchAuthorById(id)
            ?.toResponse()
            ?.let {
                ResponseEntity
                    .ok(it)
            } ?: throw NotFoundException("Author with id $id not found.")
}
