package com.bookmanager.controller

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
	scanBasePackages = [
		"com.bookmanager.application",
		"com.bookmanager.controller",
		"com.bookmanager.infra"
	]
)
class BookManagerApplication

fun main(args: Array<String>) {
	runApplication<BookManagerApplication>(*args)
}
