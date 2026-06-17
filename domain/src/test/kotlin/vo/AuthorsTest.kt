package vo

import com.bookmanager.entity.AuthorId
import com.bookmanager.vo.Authors
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class AuthorsTest {
    @Test
    fun authorIdsOfAuthorsMustNotBeEmpty() {
        // given
        val emptyAuthorIds = listOf<AuthorId>()

        // when
        // then
        val exception = assertThrows<IllegalArgumentException> {
            Authors(emptyAuthorIds)
        }
        assertThat(exception)
            .extracting { it.message }
            .isEqualTo("At least one author is required.")
    }

    @Test
    fun authorIdsOfAuthorsMustBeDistinct() {
        // given
        val duplicateAuthorIds = listOf(1L, 1L, 2L, 3L, 3L)

        // when
        val actual = Authors(duplicateAuthorIds)

        // then
        assertThat(actual)
            .extracting { it.authorIds }
            .isEqualTo(listOf(1L, 2L, 3L))
    }
}
