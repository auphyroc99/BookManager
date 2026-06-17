package com.bookmanager.controller

import com.bookmanager.application.dto.AuthorDto
import com.bookmanager.application.exception.AuthorNotFoundException
import com.bookmanager.application.exception.OptimisticLockException
import com.bookmanager.application.port.IAuthorAppService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

@WebMvcTest(controllers = [AuthorController::class])
internal class AuthorControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var authorAppService: IAuthorAppService

    @Test
    fun httpStatus201ShouldBeReturnedIfRegisterAuthorSucceeded() {
        // given
        `when`(
            authorAppService.registerAuthor(any())
        ).thenReturn(authorDto)

        // when
        // then
        mockMvc.perform(
            post("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "name": "$NAME",
                      "birthDate": "$birthDate"
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(AUTHOR_ID))
            .andExpect(jsonPath("$.name").value(NAME))
            .andExpect(jsonPath("$.birthDate").value(birthDate.toString()))
    }

    @Test
    fun httpStatus400ShouldBeReturnedIfRegisterAuthorAndIllegalArgumentExceptionThrown() {
        // given
        `when`(
            authorAppService.registerAuthor(any())
        ).thenThrow(IllegalArgumentException("Test IllegalArgumentException"))

        // when
        // then
        mockMvc.perform(
            post("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "name": " ",
                      "birthDate": "$birthDate"
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun httpStatus200ShouldBeReturnedIfUpdateAuthorSucceeded() {
        // given
        `when`(
            authorAppService.updateAuthor(any())
        ).thenReturn(authorDto)

        // when
        // then
        mockMvc.perform(
            put("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "id": $AUTHOR_ID,
                      "name": "$NAME",
                      "birthDate": "$birthDate",
                      "version": 0
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(AUTHOR_ID))
            .andExpect(jsonPath("$.name").value(NAME))
            .andExpect(jsonPath("$.birthDate").value(birthDate.toString()))
    }

    @Test
    fun httpStatus400ShouldBeReturnedIfUpdateAuthorAndIllegalArgumentExceptionThrown() {
        // given
        `when`(
            authorAppService.updateAuthor(any())
        ).thenThrow(IllegalArgumentException("Test IllegalArgumentException"))

        // when
        // then
        mockMvc.perform(
            put("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "id": $AUTHOR_ID,
                      "name": " ",
                      "birthDate": "$birthDate",
                      "version": 0
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun httpStatus404ShouldBeReturnedIfUpdateAuthorAndAuthorNotFoundExceptionThrown() {
        // given
        `when`(
            authorAppService.updateAuthor(any())
        ).thenThrow(AuthorNotFoundException("Test AuthorNotFoundException"))

        // when
        // then
        mockMvc.perform(
            put("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "id": $AUTHOR_ID,
                      "name": "$NAME",
                      "birthDate": "$birthDate",
                      "version": 0
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun httpStatus409ShouldBeReturnedIfUpdateAuthorAndOptimisticLockExceptionThrown() {
        // given
        `when`(
            authorAppService.updateAuthor(any())
        ).thenThrow(OptimisticLockException("conflict"))

        // when
        // then
        mockMvc.perform(
            put("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "id": $AUTHOR_ID,
                      "name": "$NAME",
                      "birthDate": "$birthDate",
                      "version": 0
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isConflict)
    }

    @Test
    fun httpStatus200ShouldBeReturnedIfFetchAuthorByIdSucceeded() {
        // given
        `when`(
            authorAppService.fetchAuthorById(AUTHOR_ID)
        ).thenReturn(authorDto)

        // when
        // then
        mockMvc.perform(get("/authors/$AUTHOR_ID"))
            .andExpect(jsonPath("$.id").value(AUTHOR_ID))
            .andExpect(jsonPath("$.name").value(NAME))
            .andExpect(jsonPath("$.birthDate").value(birthDate.toString()))
    }

    @Test
    fun httpStatus404ShouldBeReturnedIfFetchAuthorByIdAndAuthorDoesNotExist() {
        // given
        `when`(
            authorAppService.fetchAuthorById(AUTHOR_ID)
        ).thenReturn(null)

        // when
        // then
        mockMvc.perform(get("/authors/$AUTHOR_ID"))
            .andExpect(status().isNotFound)
    }

    companion object {
        private const val AUTHOR_ID = 100L
        private const val NAME = "Brian Kennighan"
        private val birthDate = LocalDate.parse("1942-01-01")

        private val authorDto = AuthorDto(
            id = AUTHOR_ID,
            name = NAME,
            birthDate = birthDate,
        )
    }
}
