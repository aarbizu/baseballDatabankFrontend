package util

fun upsertPlayerSql(id: String, gamesPlayed: Int, position: Int): String {
    return """
        INSERT INTO players
        VALUES ('$id', '$gamesPlayed', '$position')
    """.trimIndent()
}

val createTableSql =
    """
    DROP TABLE IF EXISTS players;
    CREATE TABLE players (
        id text,
        games int DEFAULT null,
        pos int DEFAULT null
    );
    """.trimIndent()
