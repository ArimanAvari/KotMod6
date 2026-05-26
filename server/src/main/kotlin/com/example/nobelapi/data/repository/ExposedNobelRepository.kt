package com.example.nobelapi.data.repository

import com.example.nobelapi.data.database.FavoritePrizesTable
import com.example.nobelapi.data.database.LaureatesTable
import com.example.nobelapi.data.database.PrizesTable
import com.example.nobelapi.data.database.UsersTable
import com.example.nobelapi.domain.model.AuthUser
import com.example.nobelapi.domain.model.Laureate
import com.example.nobelapi.domain.model.NobelPrize
import com.example.nobelapi.domain.repository.NobelRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ExposedNobelRepository : NobelRepository {
    override fun findUser(username: String): AuthUser? = transaction {
        UsersTable.selectAll()
            .where { UsersTable.username eq username }
            .firstOrNull()
            ?.toAuthUser()
    }

    override fun getPrizes(): List<NobelPrize> = transaction {
        PrizesTable.selectAll()
            .orderBy(PrizesTable.year to org.jetbrains.exposed.sql.SortOrder.DESC)
            .map { it.toPrize(includeLaureates = true) }
    }

    override fun getPrize(year: Int, category: String): NobelPrize? = transaction {
        findPrizeRow(year, category)?.toPrize(includeLaureates = true)
    }

    override fun getLaureates(year: Int, category: String): List<Laureate>? = transaction {
        val prizeId = findPrizeRow(year, category)?.get(PrizesTable.id) ?: return@transaction null
        laureatesFor(prizeId)
    }

    override fun getFavorites(userId: Int): List<NobelPrize> = transaction {
        (FavoritePrizesTable innerJoin PrizesTable)
            .selectAll()
            .where { FavoritePrizesTable.userId eq userId }
            .map { it.toPrize(includeLaureates = true) }
    }

    override fun addFavorite(userId: Int, year: Int, category: String): NobelPrize? = transaction {
        val prize = findPrizeRow(year, category) ?: return@transaction null
        val prizeId = prize[PrizesTable.id]
        val alreadyAdded = !FavoritePrizesTable.selectAll()
            .where { (FavoritePrizesTable.userId eq userId) and (FavoritePrizesTable.prizeId eq prizeId) }
            .empty()

        if (!alreadyAdded) {
            FavoritePrizesTable.insert {
                it[FavoritePrizesTable.userId] = userId
                it[FavoritePrizesTable.prizeId] = prizeId
            }
        }

        prize.toPrize(includeLaureates = true)
    }

    override fun removeFavorite(userId: Int, year: Int, category: String): Boolean = transaction {
        val prizeId = findPrizeRow(year, category)?.get(PrizesTable.id) ?: return@transaction false
        FavoritePrizesTable.deleteWhere {
            (FavoritePrizesTable.userId eq userId) and (FavoritePrizesTable.prizeId eq prizeId)
        } > 0
    }

    private fun findPrizeRow(year: Int, category: String): ResultRow? {
        return PrizesTable.selectAll()
            .where { (PrizesTable.year eq year) and (PrizesTable.category eq category.lowercase()) }
            .firstOrNull()
    }

    private fun ResultRow.toAuthUser(): AuthUser {
        return AuthUser(
            id = this[UsersTable.id],
            username = this[UsersTable.username],
            passwordHash = this[UsersTable.passwordHash],
            role = this[UsersTable.role]
        )
    }

    private fun ResultRow.toPrize(includeLaureates: Boolean): NobelPrize {
        val prizeId = this[PrizesTable.id]
        return NobelPrize(
            id = prizeId,
            year = this[PrizesTable.year],
            category = this[PrizesTable.category],
            description = this[PrizesTable.description],
            rawJson = this[PrizesTable.rawJson],
            laureates = if (includeLaureates) laureatesFor(prizeId) else emptyList()
        )
    }

    private fun laureatesFor(prizeId: Int): List<Laureate> {
        return LaureatesTable.selectAll()
            .where { LaureatesTable.prizeId eq prizeId }
            .map {
                Laureate(
                    id = it[LaureatesTable.id],
                    fullName = it[LaureatesTable.fullName],
                    birthCountry = it[LaureatesTable.birthCountry],
                    motivation = it[LaureatesTable.motivation]
                )
            }
    }
}
