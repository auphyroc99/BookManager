package com.book_manager.repository

import com.book_manager.domain.entity.AuthorEntity
import com.book_manager.domain.entity.AuthorId
import com.book_manager.domain.entity.AuthorSchema
import com.book_manager.domain.entity.NewAuthorEntity
import com.book_manager.domain.port.IAuthorRepository
import com.book_manager.domain.vo.BirthDate
import com.book_manager.domain.vo.Version
import com.book_manager.jooq.Tables.AUTHOR
import org.jooq.DSLContext
import org.jooq.impl.DSL.noCondition
import org.springframework.stereotype.Repository

@Repository
internal class AuthorRepository(
    private val dsl: DSLContext
) : IAuthorRepository {
    override fun findById(id: AuthorId): AuthorEntity {
        return findSingleById(id)
    }

    override fun save(author: AuthorSchema): AuthorEntity {
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
                return findSingleById(author.id)
            }

            is NewAuthorEntity -> {
                return dsl.insertInto(AUTHOR)
                    .set(AUTHOR.NAME, author.name)
                    .set(AUTHOR.BIRTH_DATE, author.birthDate.date)
                    .set(AUTHOR.VERSION, 0)
                    .returning(AUTHOR.ID)
                    .fetchSingle()
                    .get(AUTHOR.ID)
                    .let {
                        findSingleById(it)
                    }
            }
        }
    }

    private fun findSingleById(id: AuthorId): AuthorEntity =
        dsl.select()
            .from(AUTHOR)
            .where(AUTHOR.ID.eq(id))
            .fetchOne()
            ?.let { authorRecord ->
                AuthorEntity(
                    id = authorRecord.get(AUTHOR.ID),
                    name = authorRecord.get(AUTHOR.NAME),
                    birthDate = BirthDate(authorRecord.get(AUTHOR.BIRTH_DATE)),
                    version = Version(authorRecord.get(AUTHOR.VERSION))
                )
            } ?: throw RuntimeException()
}