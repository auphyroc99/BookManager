package com.bookmanager.vo

data class Version(
    val value: Int,
) {
    fun next() = Version(value + 1)
}
