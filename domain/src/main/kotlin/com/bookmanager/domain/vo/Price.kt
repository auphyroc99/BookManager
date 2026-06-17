package com.bookmanager.domain.vo

data class Price(
    val value: Int,
) {
    init {
        require(value >= 0) {
            "`value` of `Price` must be a non-negative integer. `value`: $value."
        }
    }
}
