package com.example.nobelapi.data.database

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object UsersTable : Table("users") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 64).uniqueIndex()
    val passwordHash = varchar("password_hash", 120)
    val role = varchar("role", 32)

    override val primaryKey = PrimaryKey(id)
}

object PrizesTable : Table("prizes") {
    val id = integer("id").autoIncrement()
    val year = integer("year")
    val category = varchar("category", 48)
    val description = text("description")
    val rawJson = text("raw_json")

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(year, category)
    }
}

object LaureatesTable : Table("laureates") {
    val id = integer("id").autoIncrement()
    val prizeId = integer("prize_id").references(PrizesTable.id, onDelete = ReferenceOption.CASCADE)
    val fullName = varchar("full_name", 160)
    val birthCountry = varchar("birth_country", 120)
    val motivation = text("motivation")

    override val primaryKey = PrimaryKey(id)
}

object FavoritePrizesTable : Table("favorite_prizes") {
    val userId = integer("user_id").references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
    val prizeId = integer("prize_id").references(PrizesTable.id, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(userId, prizeId)
}
