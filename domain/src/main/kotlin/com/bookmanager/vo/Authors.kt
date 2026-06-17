package com.bookmanager.vo

import com.bookmanager.entity.AuthorId

class Authors private constructor(
    val authorIds: List<AuthorId>,
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
