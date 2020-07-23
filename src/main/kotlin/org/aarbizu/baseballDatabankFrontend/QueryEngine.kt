package org.aarbizu.baseballDatabankFrontend

import com.google.common.base.Stopwatch
import java.sql.DriverManager
import org.aarbizu.baseballDatabankFrontend.config.dbPassword
import org.aarbizu.baseballDatabankFrontend.config.dbUrl
import org.aarbizu.baseballDatabankFrontend.config.dbUser
import org.slf4j.LoggerFactory

object DB {
    val conn by lazy {
        DriverManager.getConnection(dbUrl, dbUser, dbPassword)
    }
}

data class PlayerBasic(val name: String, val born: String, val debut: String, val finalgame: String)
data class Player(
    val name: String,
    val born: String,
    val debut: String,
    val finalgame: String,
    val playerId: String,
    val bbrefid: String
)

val playerNamesByLength = """
    SELECT
        COALESCE(namegiven, 'unknown') || ' ' || COALESCE(namelast, 'unknown') as name,
        COALESCE(birthyear, -1) || '-' || COALESCE(birthmonth, -1) || '-' || COALESCE(birthday, -1) as born,
        COALESCE(debut, 'unknown') as debut,
        COALESCE(finalgame, 'unknown') as finalgame,
        COALESCE(playerid, 'unknown') as playerid,
        COALESCE(bbrefid, 'unknown') as bbrefid
    FROM people
    WHERE length(namelast) = ?
    ORDER BY playerid
""".trimIndent()

val playerName = """
    SELECT
        COALESCE(namegiven, 'unknown') || ' ' || COALESCE(namelast, 'unknown') as name,
        COALESCE(birthyear, -1) || '-' || COALESCE(birthmonth, -1) || '-' || COALESCE(birthday, -1) as born,
        COALESCE(debut, 'unknown') as debut,
        COALESCE(finalgame, 'unknown') as finalgame
    FROM people
    WHERE namelast = ?
""".trimIndent()

private val logger = LoggerFactory.getLogger("QueryEngine")

class QueryEngine {
    fun playerNamesByLength(length: String): List<Player> {
        val stmt = DB.conn.prepareStatement(playerNamesByLength)
        stmt.setInt(1, length.toInt())
        val players: MutableList<Player> = mutableListOf()

        val timer = Stopwatch.createStarted()
        val rs = stmt.executeQuery()
        logger.info("exec query: $timer")
        timer.reset()
        while (rs.next()) {
            players.add(
                Player(
                    rs.getString("name"),
                    rs.getString("born"),
                    rs.getString("debut"),
                    rs.getString("finalgame"),
                    rs.getString("playerid"),
                    rs.getString("bbrefid")
            ))
        }
        logger.info("extract resultSet: $timer")
        return players
    }
}
