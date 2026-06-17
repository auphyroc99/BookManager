package com.bookmanager.application.exception

class BookNotFoundException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}
