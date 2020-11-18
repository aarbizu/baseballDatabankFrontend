package util

import java.sql.DriverManager
import org.testcontainers.containers.PostgreSQLContainer

fun setupTestDatabase(db: PostgreSQLContainer<*>) {
    DriverManager.getConnection(db.jdbcUrl, db.username, db.password).use { conn ->
        conn.createStatement().use { stmt ->
            stmt.execute(createTableSql)
        }
    }
}

fun upsertPlayer(db: PostgreSQLContainer<*>, id: String, gamesPlayed: Int, position: Int) {
    DriverManager.getConnection(db.jdbcUrl, db.username, db.password).use { conn ->
        conn.createStatement().use { stmt ->
            stmt.execute(upsertPlayerSql(id, gamesPlayed, position))
        }
    }
}

fun upsertPlayerSql(id: String, gamesPlayed: Int, position: Int): String {
    return """
        INSERT INTO players
        VALUES ('$id', '$gamesPlayed', '$position')
    """.trimIndent()
}

val createTableSql = """
    DROP TABLE IF EXISTS players;
    CREATE TABLE players (
        id text,
        games int DEFAULT null,
        pos int DEFAULT null
    );
""".trimIndent()
