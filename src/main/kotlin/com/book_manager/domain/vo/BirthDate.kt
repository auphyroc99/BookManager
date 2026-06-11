package com.book_manager.domain.vo

import java.time.LocalDate

data class BirthDate(
    val date: LocalDate,
) {
    init {
        require(date.isAfter(LocalDate.now())) {
            "`date` of `BirthDate` must be after current date. `date`: $date."
        }
    }
}
