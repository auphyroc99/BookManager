package com.bookmanager.application

import com.bookmanager.domain.port.IAuthorRepository
import com.bookmanager.domain.port.IBookQueryService
import com.bookmanager.domain.port.IBookRepository
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean

@SpringBootTest(classes = [ApplicationTestConfig::class])
internal class BaseAppServiceTest {
    @MockitoBean
    protected lateinit var authorRepository: IAuthorRepository

    @MockitoBean
    protected lateinit var bookRepository: IBookRepository

    @MockitoBean
    protected lateinit var bookQueryService: IBookQueryService
}
