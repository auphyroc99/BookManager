package com.bookmanager.vo

import com.bookmanager.vo.Price
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class PriceTest {
    @Test
    fun valueOfPriceMustBeNonNegative() {
        // given
        val negativePrice = -1

        // when
        // then
        val exception = assertThrows<IllegalArgumentException> {
            Price(negativePrice)
        }
        assertThat(exception)
            .extracting { it.message }
            .isEqualTo("`value` of `Price` must be a non-negative integer. `value`: $negativePrice.")
    }
}
