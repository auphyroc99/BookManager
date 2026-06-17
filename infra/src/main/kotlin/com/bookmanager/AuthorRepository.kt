package com.bookmanager

import com.bookmanager.entity.AuthorEntity
import com.bookmanager.entity.AuthorId
import com.bookmanager.entity.AuthorSchema
import com.bookmanager.entity.NewAuthorEntity
import com.bookmanager.jooq.Tables.AUTHOR
import com.bookmanager.port.IAuthorRepository
import com.bookmanager.vo.BirthDate
import com.bookmanager.vo.Version
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
