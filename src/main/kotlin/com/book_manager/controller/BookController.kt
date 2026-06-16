package com.book_manager.controller

import com.book_manager.application.port.IBookAppService
import org.springframework.stereotype.Controller

@Controller(value = "/book")
internal class BookController(
    private val bookAppService: IBookAppService,
) {
}