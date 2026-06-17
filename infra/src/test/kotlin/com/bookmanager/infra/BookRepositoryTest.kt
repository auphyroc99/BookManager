package com.bookmanager.infra

import com.bookmanager.domain.entity.NewAuthorEntity
import com.bookmanager.domain.entity.NewBookEntity
import com.bookmanager.domain.port.IAuthorRepository
import com.bookmanager.domain.port.IBookRepository
import com.bookmanager.domain.vo.Authors
import com.bookmanager.domain.vo.BirthDate
import com.bookmanager.domain.vo.BookPublicationStatus
import com.bookmanager.domain.vo.Price
import com.bookmanager.domain.vo.Version
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@SpringBootTest(classes = [InfraTestConfig::class])
@Transactional
internal class BookRepositoryTest {
    @Autowired
    private lateinit var bookRepository: IBookRepository

    @Autowired
    private lateinit var authorRepository: IAuthorRepository

    @Test
    fun bookShouldBeFoundByIdIfExists() {
        // given
        val authorIds = authorsPrepared
            .map {
                authorRepository.save(it)
            }
            .map {
                it.id
            }
        val targetBook = NewBookEntity(
            title = "The C Programming Language",
            price = Price(2500),
            authors = Authors(listOf(authorIds[0], authorIds[1])),
            publicationStatus = BookPublicationStatus.PUBLISHED,
        ).let {
            bookRepository.save(it)
        }
        val anotherBook = NewBookEntity(
            title = "The Z Programming Language",
            price = Price(9999),
            authors = Authors(listOf(authorIds[0], authorIds[2])),
            publicationStatus = BookPublicationStatus.NOT_PUBLISHED,
        ).let {
            bookRepository.save(it)
        }

        // when
        val actual = bookRepository.findById(targetBook.id)

        // then
        requireNotNull(actual)
        assertThat(actual)
            .satisfies({
                assertThat(it.id)
                    .isEqualTo(targetBook.id)
                    .isNotEqualTo(anotherBook.id)
                assertThat(it.title)
                    .isEqualTo(targetBook.title)
                    .isNotEqualTo(anotherBook.title)
                assertThat(it.price)
                    .isEqualTo(targetBook.price)
                    .isNotEqualTo(anotherBook.price)
                assertThat(it.authors)
                    .usingRecursiveComparison()
                    .isEqualTo(targetBook.authors)
                    .isNotEqualTo(anotherBook.authors)
                assertThat(it.publicationStatus)
                    .isEqualTo(targetBook.publicationStatus)
                    .isNotEqualTo(anotherBook.publicationStatus)
                assertThat(it.version).isEqualTo(Version(0))
            })
    }

    @Test
    fun bookShouldNotBeFoundByIdIfDoesNotExist() {
        // given
        val nonExistingId = 999_999L

        // when
        val actual = bookRepository.findById(nonExistingId)

        // then
        assertThat(actual).isNull()
    }

    @Test
    fun bookShouldBeSavedWhenCreated() {
        // given
        val authorIds = authorsPrepared
            .map {
                authorRepository.save(it)
            }
            .map {
                it.id
            }
        val targetBookBeforeSaved = NewBookEntity(
            title = "The C Programming Language",
            price = Price(2500),
            authors = Authors(listOf(authorIds[0], authorIds[1])),
            publicationStatus = BookPublicationStatus.PUBLISHED,
        )

        // when
        val result = bookRepository.save(targetBookBeforeSaved)

        // then
        val actual = bookRepository.findById(result.id)
        requireNotNull(actual)
        assertThat(actual)
            .satisfies({
                assertThat(it.title)
                    .isEqualTo(targetBookBeforeSaved.title)
                assertThat(it.price)
                    .isEqualTo(targetBookBeforeSaved.price)
                assertThat(it.authors)
                    .usingRecursiveComparison()
                    .isEqualTo(targetBookBeforeSaved.authors)
                assertThat(it.publicationStatus)
                    .isEqualTo(targetBookBeforeSaved.publicationStatus)
                assertThat(it.version).isEqualTo(Version(0))
            })
    }

    @Test
    fun authorShouldBeSavedWhenUpdated() {
        // given
        val authorIds = authorsPrepared
            .map {
                authorRepository.save(it)
            }
            .map {
                it.id
            }
        val targetBookBeforeSaved = NewBookEntity(
            title = "The Z Programming Language",
            price = Price(9999),
            authors = Authors(listOf(authorIds[0], authorIds[2])),
            publicationStatus = BookPublicationStatus.NOT_PUBLISHED,
        ).let {
            bookRepository.save(it)
        }
        val targetBookAfterSaved = targetBookBeforeSaved
            .updateTitle("The Z Programming Language Updated")
            .updatePrice(Price(10000))
            .updateAuthorIds(Authors(listOf(authorIds[1], authorIds[2])))
            .publish()

        // when
        val result = bookRepository.save(targetBookAfterSaved)

        // then
        val actual = bookRepository.findById(result.id)
        requireNotNull(actual)
        assertThat(actual)
            .satisfies({
                assertThat(it.id)
                    .isEqualTo(targetBookAfterSaved.id)
                    .isEqualTo(targetBookBeforeSaved.id)
                assertThat(it.title)
                    .isEqualTo(targetBookAfterSaved.title)
                    .isNotEqualTo(targetBookBeforeSaved.title)
                assertThat(it.price)
                    .isEqualTo(targetBookAfterSaved.price)
                    .isNotEqualTo(targetBookBeforeSaved.price)
                assertThat(it.authors)
                    .usingRecursiveComparison()
                    .isEqualTo(targetBookAfterSaved.authors)
                    .isNotEqualTo(targetBookBeforeSaved.authors)
                assertThat(it.publicationStatus)
                    .isEqualTo(targetBookAfterSaved.publicationStatus)
                    .isNotEqualTo(targetBookBeforeSaved.publicationStatus)
                assertThat(it.version).isEqualTo(Version(1))
            })
    }

    companion object {
        private val authorsPrepared = listOf(
            NewAuthorEntity(
                name = "Brian Kennighan",
                birthDate = BirthDate(LocalDate.parse("1942-01-01")),
            ),
            NewAuthorEntity(
                name = "Dennis Ritchie",
                birthDate = BirthDate(LocalDate.parse("1941-09-09")),
            ),
            NewAuthorEntity(
                name = "Rob Pike",
                birthDate = BirthDate(LocalDate.parse("1956-01-01")),
            ),
        )
    }
}