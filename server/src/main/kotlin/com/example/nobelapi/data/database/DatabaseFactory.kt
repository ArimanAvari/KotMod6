package com.example.nobelapi.data.database

import at.favre.lib.crypto.bcrypt.BCrypt
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

object DatabaseFactory {
    fun init() {
        Database.connect(HikariDataSource(buildConfig()))

        transaction {
            SchemaUtils.create(UsersTable, PrizesTable, LaureatesTable, FavoritePrizesTable)
            seedUser()
            seedPrizes()
        }
    }

    private fun buildConfig(): HikariConfig {
        val databaseUrl = System.getenv("JDBC_DATABASE_URL")
            ?: System.getenv("DATABASE_URL")
            ?: System.getenv("NEON_DATABASE_URL")

        return if (databaseUrl.isNullOrBlank()) {
            HikariConfig().apply {
                jdbcUrl = "jdbc:h2:mem:nobel;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1"
                driverClassName = "org.h2.Driver"
                username = "sa"
                password = ""
                maximumPoolSize = 5
            }
        } else {
            val parsed = JdbcSettings.from(databaseUrl)
            HikariConfig().apply {
                jdbcUrl = parsed.jdbcUrl
                driverClassName = "org.postgresql.Driver"
                username = parsed.username ?: System.getenv("DB_USER")
                password = parsed.password ?: System.getenv("DB_PASSWORD")
                maximumPoolSize = 10
                minimumIdle = 2
                connectionTimeout = 30_000
                idleTimeout = 300_000
                maxLifetime = 1_800_000
            }
        }
    }

    private fun seedUser() {
        if (UsersTable.selectAll().empty()) {
            UsersTable.insert {
                it[username] = "student"
                it[passwordHash] = BCrypt.withDefaults().hashToString(12, "studentpass".toCharArray())
                it[role] = "USER"
            }
        }
    }

    private fun seedPrizes() {
        if (!PrizesTable.selectAll().empty()) return

        val physics = insertPrize(
            year = 2023,
            category = "physics",
            description = "Experimental methods that generate attosecond pulses of light.",
            rawJson = """{"source":"seed","category":"physics","year":2023}"""
        )
        insertLaureate(physics, "Pierre Agostini", "France", "for experimental methods that generate attosecond pulses of light")
        insertLaureate(physics, "Ferenc Krausz", "Hungary", "for experimental methods that generate attosecond pulses of light")
        insertLaureate(physics, "Anne L'Huillier", "France", "for experimental methods that generate attosecond pulses of light")

        val chemistry = insertPrize(
            year = 2023,
            category = "chemistry",
            description = "Discovery and synthesis of quantum dots.",
            rawJson = """{"source":"seed","category":"chemistry","year":2023}"""
        )
        insertLaureate(chemistry, "Moungi G. Bawendi", "France", "for the discovery and synthesis of quantum dots")
        insertLaureate(chemistry, "Louis E. Brus", "United States", "for the discovery and synthesis of quantum dots")
        insertLaureate(chemistry, "Aleksey Yekimov", "Russia", "for the discovery and synthesis of quantum dots")

        val peace = insertPrize(
            year = 2022,
            category = "peace",
            description = "Human rights defenders and organizations from Eastern Europe.",
            rawJson = """{"source":"seed","category":"peace","year":2022}"""
        )
        insertLaureate(peace, "Ales Bialiatski", "Belarus", "for protecting fundamental rights of citizens")
        insertLaureate(peace, "Memorial", "Russia", "for documenting human rights abuses")
        insertLaureate(peace, "Center for Civil Liberties", "Ukraine", "for documenting war crimes and protecting human rights")
    }

    private fun insertPrize(year: Int, category: String, description: String, rawJson: String): Int {
        return PrizesTable.insert {
            it[PrizesTable.year] = year
            it[PrizesTable.category] = category
            it[PrizesTable.description] = description
            it[PrizesTable.rawJson] = rawJson
        } get PrizesTable.id
    }

    private fun insertLaureate(prizeId: Int, fullName: String, country: String, motivation: String) {
        LaureatesTable.insert {
            it[LaureatesTable.prizeId] = prizeId
            it[LaureatesTable.fullName] = fullName
            it[birthCountry] = country
            it[LaureatesTable.motivation] = motivation
        }
    }
}

private data class JdbcSettings(
    val jdbcUrl: String,
    val username: String?,
    val password: String?
) {
    companion object {
        fun from(value: String): JdbcSettings {
            if (value.startsWith("jdbc:", ignoreCase = true)) {
                return JdbcSettings(value, null, null)
            }

            val uri = URI(value)
            val userInfo = uri.userInfo?.split(":", limit = 2)
            val user = userInfo?.getOrNull(0)?.decodeUrl()
            val pass = userInfo?.getOrNull(1)?.decodeUrl()
            val query = uri.rawQuery?.let { "?$it" }.orEmpty()
            val jdbc = "jdbc:postgresql://${uri.host}:${uri.port.takeIf { it > 0 } ?: 5432}${uri.path}$query"

            return JdbcSettings(jdbc, user, pass)
        }
    }
}

private fun String.decodeUrl(): String = URLDecoder.decode(this, StandardCharsets.UTF_8)
