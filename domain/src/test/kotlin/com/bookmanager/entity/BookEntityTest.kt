package com.bookmanager.entity

import com.bookmanager.entity.BookEntity
import com.bookmanager.entity.NewBookEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock

internal class BookEntityTest {
    @Test
    fun titleOfNewBookEntityMustNotBeBlank() {
        // given
        val blankTitle = " "

        // when
        // then
        val exception = assertThrows<IllegalArgumentException> {
            NewBookEntity(
                title = blankTitle,
                price = mock(),
                authors = mock(),
                publicationStatus = mock(),
            )
        }
        assertThat(exception)
            .extracting { it.message }
            .isEqualTo("`title` of `book` must not be blank.")
    }

    @Test
    fun titleOfBookEntityMustNotBeBlank() {
        // given
        val blankTitle = " "

        // when
        // then
        val exception = assertThrows<IllegalArgumentException> {
            BookEntity(
                id = 1L,
                title = blankTitle,
                price = mock(),
                authors = mock(),
                publicationStatus = mock(),
                version = mock(),
            )
        }
        assertThat(exception)
            .extracting { it.message }
            .isEqualTo("`title` of `book` must not be blank.")
    }

    @Test
    fun titleOfBookEntityMustNotBeUpdatedToBlank() {
        // given
        val targetBookEntity = BookEntity(
            id = 1L,
            title = "not blank",
            price = mock(),
            authors = mock(),
            publicationStatus = mock(),
            version = mock(),
        )
        val blankTitle = " "

        // when
        // then
        val exception = assertThrows<IllegalArgumentException> {
            targetBookEntity
                .updateTitle(blankTitle)
        }
        assertThat(exception)
            .extracting { it.message }
            .isEqualTo("`title` of `book` must not be blank.")
    }
}
