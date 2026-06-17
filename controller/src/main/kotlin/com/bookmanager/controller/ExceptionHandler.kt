package com.bookmanager.controller

import com.bookmanager.controller.exception.BadRequestException
import com.bookmanager.controller.exception.ConflictException
import com.bookmanager.controller.exception.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(
        ex: BadRequestException
    ): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    code = "BAD_REQUEST",
                    message = ex.message ?: "Bad request"
                )
            )

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(
        ex: NotFoundException
    ): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                ErrorResponse(
                    code = "NOT_FOUND",
                    message = ex.message ?: "Not found"
                )
            )

    @ExceptionHandler(ConflictException::class)
    fun handleConflict(
        ex: ConflictException
    ): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(
                ErrorResponse(
                    code = "CONFLICT",
                    message = ex.message ?: "Conflict occurred"
                )
            )

    @ExceptionHandler(Throwable::class)
    fun handleUnexpected(
        ex: Throwable
    ): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ErrorResponse(
                    code = "INTERNAL_SERVER_ERROR",
                    message = ex.message ?: "Unexpected error occurred"
                )
            )
}

data class ErrorResponse(
    val code: String,
    val message: String
)
