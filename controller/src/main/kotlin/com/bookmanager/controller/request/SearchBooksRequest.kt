package com.bookmanager.controller.request

import com.bookmanager.application.param.SearchBooksParam

data class SearchBooksRequest(
    val authorId: Long,
) {
    fun toParam(): SearchBooksParam =
        SearchBooksParam(
            authorId = this.authorId,
        )
}
