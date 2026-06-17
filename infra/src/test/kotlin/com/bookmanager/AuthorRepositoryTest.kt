package com.bookmanager

import com.bookmanager.entity.NewAuthorEntity
import com.bookmanager.port.IAuthorRepository
import com.bookmanager.vo.BirthDate
import com.bookmanager.vo.Version
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@SpringBootTest(classes = [InfraTestConfig::class])
@Transactional
internal class AuthorRepositoryTest {
    @Autowired
    private lateinit var authorRepository: IAuthorRepository

    @Test
    fun authorShouldBeFoundByIdIfExists() {
        // given
        val targetAuthor = NewAuthorEntity(
            name = "Brian Kennighan",
            birthDate = BirthDate(LocalDate.parse("1942-01-01")),
        ).let {
            authorRepository.save(it)
        }
        val anotherAuthor = NewAuthorEntity(
            name = "Dennis Ritchie",
            birthDate = BirthDate(LocalDate.parse("1941-09-09")),
        ).let {
            authorRepository.save(it)
        }

        // when
        val actual = authorRepository.findById(targetAuthor.id)

        // then
        requireNotNull(actual)
        assertThat(actual)
            .satisfies({
                assertThat(it.id)
                    .isEqualTo(targetAuthor.id)
                    .isNotEqualTo(anotherAuthor.id)
                assertThat(it.name)
                    .isEqualTo(targetAuthor.name)
                    .isNotEqualTo(anotherAuthor.name)
                assertThat(it.birthDate)
                    .isEqualTo(targetAuthor.birthDate)
                    .isNotEqualTo(anotherAuthor.birthDate)
                assertThat(it.version).isEqualTo(Version(0))
            })
    }

    @Test
    fun authorShouldNotBeFoundByIdIfDoesNotExist() {
        // given
        val nonExistingId = 999_999L

        // when
        val actual = authorRepository.findById(nonExistingId)

        // then
        assertThat(actual).isNull()
    }

    @Test
    fun authorShouldBeSavedWhenCreated() {
        // given
        val targetAuthorBeforeSaved = NewAuthorEntity(
            name = "Brian Kennighan",
            birthDate = BirthDate(LocalDate.parse("1942-01-01")),
        )

        // when
        val result = authorRepository.save(targetAuthorBeforeSaved)

        // then
        val actual = authorRepository.findById(result.id)
        requireNotNull(actual)
        assertThat(actual)
            .satisfies({
                assertThat(it.name)
                    .isEqualTo(targetAuthorBeforeSaved.name)
                assertThat(it.birthDate)
                    .isEqualTo(targetAuthorBeforeSaved.birthDate)
                assertThat(it.version).isEqualTo(Version(0))
            })
    }

    @Test
    fun authorShouldBeSavedWhenUpdated() {
        // given
        val targetAuthorBeforeSaved = NewAuthorEntity(
            name = "Brian Kennighan",
            birthDate = BirthDate(LocalDate.parse("1942-01-01")),
        ).let {
            authorRepository.save(it)
        }
        val targetAuthorAfterSaved = targetAuthorBeforeSaved
            .updateName("Brian Kennighan Updated")
            .updateBirthDate(BirthDate(LocalDate.parse("1941-12-31")))

        // when
        val result = authorRepository.save(targetAuthorAfterSaved)

        // then
        val actual = authorRepository.findById(result.id)
        requireNotNull(actual)
        assertThat(actual)
            .satisfies({
                assertThat(it.id)
                    .isEqualTo(targetAuthorAfterSaved.id)
                    .isEqualTo(targetAuthorBeforeSaved.id)
                assertThat(it.name)
                    .isEqualTo(targetAuthorAfterSaved.name)
                    .isNotEqualTo(targetAuthorBeforeSaved.name)
                assertThat(it.birthDate)
                    .isEqualTo(targetAuthorAfterSaved.birthDate)
                    .isNotEqualTo(targetAuthorBeforeSaved.birthDate)
                assertThat(it.version).isEqualTo(Version(1))
            })
    }
}
