package com.book_manager.repository

import com.book_manager.domain.entity.AuthorEntity
import com.book_manager.domain.entity.AuthorSchema
import com.book_manager.domain.entity.NewAuthorEntity
import com.book_manager.domain.repository.IAuthorRepository
import com.book_manager.jooq.Tables.AUTHOR
import org.jooq.DSLContext
import org.jooq.impl.DSL.noCondition
import org.springframework.stereotype.Repository

@Repository
class AuthorRepository(
    private val dsl: DSLContext
) : IAuthorRepository {

    override fun save(author: AuthorSchema) {

        when (author) {
            is AuthorEntity -> {
                dsl.update(AUTHOR)
                    .set(AUTHOR.NAME, author.name)
                    .set(AUTHOR.BIRTH_DATE, author.birthDate.date)
                    .set(AUTHOR.VERSION, author.version.next().value)
                    .where(
                        noCondition()
                            .and(AUTHOR.ID.eq(author.id))
                            .and(AUTHOR.VERSION.eq(author.version.value))
                    )
                    .execute()
                    .let { countOfRows ->
                        if (countOfRows < 1) {
                            throw RuntimeException()
                        }
                    }
            }

            is NewAuthorEntity -> {
                dsl.insertInto(AUTHOR)
                    .set(AUTHOR.NAME, author.name)
                    .set(AUTHOR.BIRTH_DATE, author.birthDate.date)
                    .set(AUTHOR.VERSION, 0)
                    .execute()
            }
        }
    }
}