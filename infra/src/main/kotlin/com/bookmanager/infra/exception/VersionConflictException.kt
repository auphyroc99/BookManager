package com.bookmanager.infra.exception

class VersionConflictException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}
