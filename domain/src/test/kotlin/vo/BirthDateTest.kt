package vo

import com.bookmanager.vo.BirthDate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

internal class BirthDateTest {
    @Test
    fun dateOfBirthDateMustBeDateInThePast() {
        // given
        val dateInTheFuture = LocalDate.of(9999, 12, 31)

        // when
        // then
        val exception = assertThrows<IllegalArgumentException> {
            BirthDate(dateInTheFuture)
        }
        assertThat(exception)
            .extracting { it.message }
            .isEqualTo("`date` of `BirthDate` must be before current date. `date`: $dateInTheFuture.")
    }
}
