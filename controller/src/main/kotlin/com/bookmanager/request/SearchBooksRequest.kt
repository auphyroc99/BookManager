package com.bookmanager.request

import com.bookmanager.param.SearchBooksParam

data class SearchBooksRequest(
    val authorId: Long,
) {
    fun toParam(): SearchBooksParam =
        SearchBooksParam(
            authorId = this.authorId,
        )
}
