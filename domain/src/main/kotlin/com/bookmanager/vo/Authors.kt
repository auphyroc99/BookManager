package com.bookmanager.vo

class Authors private constructor(
    val authorIds: List<Long>,
) {
    init {
        require(authorIds.isNotEmpty()) {
            "At least one author is required."
        }
    }

    companion object {
        operator fun invoke(authorIds: List<Long>): Authors =
            Authors(authorIds.distinct())
    }
}
