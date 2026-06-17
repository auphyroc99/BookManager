package com.bookmanager.application

import com.bookmanager.application.command.RegisterAuthorCommand
import com.bookmanager.application.command.UpdateAuthorCommand
import com.bookmanager.application.dto.AuthorDto
import com.bookmanager.application.exception.AuthorNotFoundException
import com.bookmanager.application.port.IAuthorAppService
import com.bookmanager.domain.entity.AuthorEntity
import com.bookmanager.domain.entity.NewAuthorEntity
import com.bookmanager.domain.vo.BirthDate
import com.bookmanager.domain.vo.Version
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.never
import org.mockito.Mockito.only
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate

internal class AuthorAppServiceTest : BaseAppServiceTest() {
    @Autowired
    private lateinit var authorAppService: IAuthorAppService

    @Test
    fun authorShouldBeSavedWhenRegisterAuthor() {
        // given
        `when`(
            authorRepository.save(newAuthorEntity)
        ).thenReturn(authorEntity)

        // when
        val actual = authorAppService.registerAuthor(
            RegisterAuthorCommand(
                name = NAME,
                birthDate = birthDate,
            )
        )

        // then
        assertThat(actual)
            .isEqualTo(authorDto)
        verify(authorRepository, only())
            .save(newAuthorEntity)
    }

    @Test
    fun authorShouldBeSavedWhenUpdateAuthorIfAuthorExists() {
        // given
        `when`(
            authorRepository.findById(AUTHOR_ID)
        ).thenReturn(authorEntity)
        val authorEntityUpdated = authorEntity.updateName(NAME_UPDATED)
        `when`(
            authorRepository.save(
                authorEntityUpdated
            )
        ).thenReturn(
            authorEntityUpdated
        )

        // when
        val actual = authorAppService.updateAuthor(
            UpdateAuthorCommand(
                id = AUTHOR_ID,
                name = NAME_UPDATED,
                birthDate = birthDate,
            )
        )

        // then
        assertThat(actual)
            .isEqualTo(authorDto.copy(name = NAME_UPDATED))
        verify(authorRepository, times(1))
            .findById(AUTHOR_ID)
        verify(authorRepository, times(1))
            .save(authorEntityUpdated)
    }

    @Test
    fun exceptionShouldBeThrownWhenUpdateAuthorIfAuthorDoesNotExist() {
        // given
        `when`(
            authorRepository.findById(AUTHOR_ID)
        ).thenReturn(null)

        // when
        // then
        assertThrows<AuthorNotFoundException> {
            authorAppService.updateAuthor(
                UpdateAuthorCommand(
                    id = AUTHOR_ID,
                    name = NAME_UPDATED,
                    birthDate = birthDate,
                )
            )
        }
        verify(authorRepository, only())
            .findById(AUTHOR_ID)
        verify(authorRepository, never())
            .save(any())
    }

    companion object {
        private const val AUTHOR_ID = 100L
        private const val NAME = "Brian Kennighan"
        private const val NAME_UPDATED = "Brian Kennighan Updated"
        private val birthDate = LocalDate.parse("1942-01-01")

        private val newAuthorEntity = NewAuthorEntity(
            name = NAME,
            birthDate = BirthDate(birthDate),
        )

        private val authorEntity = AuthorEntity(
            id = AUTHOR_ID,
            name = NAME,
            birthDate = BirthDate(birthDate),
            version = Version(0),
        )

        private val authorDto = AuthorDto(
            id = AUTHOR_ID,
            name = NAME,
            birthDate = birthDate,
        )
    }
}