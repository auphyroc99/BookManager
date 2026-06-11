package com.book_manager.domain.vo

data class Version(
    val value: Int,
) {
    fun next() = Version(value + 1)
}
