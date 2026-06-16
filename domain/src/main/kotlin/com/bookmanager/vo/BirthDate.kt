package com.bookmanager.vo

import java.time.LocalDate

data class BirthDate(
    val date: LocalDate,
) {
    init {
        require(date.isBefore(LocalDate.now())) {
            "`date` of `BirthDate` must be before current date. `date`: $date."
        }
    }
}
