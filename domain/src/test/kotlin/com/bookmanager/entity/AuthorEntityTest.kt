package com.bookmanager.entity

import com.bookmanager.entity.AuthorEntity
import com.bookmanager.entity.NewAuthorEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock

internal class AuthorEntityTest {
    @Test
    fun nameOfNewAuthorEntityMustNotBeBlank() {
        // given
        val blankName = " "

        // when
        // then
        val exception = assertThrows<IllegalArgumentException> {
            NewAuthorEntity(
                name = blankName,
                birthDate = mock(),
            )
        }
        assertThat(exception)
            .extracting { it.message }
            .isEqualTo("`name` of `author` must not be blank.")
    }

    @Test
    fun nameOfAuthorEntityMustNotBeBlank() {
        // given
        val blankName = " "

        // when
        // then
        val exception = assertThrows<IllegalArgumentException> {
            AuthorEntity(
                id = 1L,
                name = blankName,
                birthDate = mock(),
                version = mock(),
            )
        }
        assertThat(exception)
            .extracting { it.message }
            .isEqualTo("`name` of `author` must not be blank.")
    }

    @Test
    fun nameOfAuthorEntityMustNotBeUpdatedToBlank() {
        // given
        val targetAuthorEntity = AuthorEntity(
            id = 1L,
            name = "not blank",
            birthDate = mock(),
            version = mock(),
        )
        val blankName = " "

        // when
        // then
        val exception = assertThrows<IllegalArgumentException> {
            targetAuthorEntity
                .updateName(blankName)
        }
        assertThat(exception)
            .extracting { it.message }
            .isEqualTo("`name` of `author` must not be blank.")
    }
}