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

// TODO move these into their own file for data types
abstract class TableRecord() {
    abstract fun headers(): List<String>
    abstract fun cells(): List<String>
}
data class PlayerBasic(
    val name: String,
    val born: String,
    val debut: String,
    val finalgame: String
) : TableRecord() {

    override fun headers(): List<String> {
        return listOf("Name", "Born", "Debut", "Final Game")
    }

    override fun cells(): List<String> {
        return listOf(name, born, debut, finalgame)
    }
}

data class Player(
    val name: String,
    val born: String,
    val debut: String,
    val finalgame: String,
    val playerId: String,
    val bbrefid: String
) : TableRecord() {

    override fun headers(): List<String> {
        return listOf("Name", "Born", "Debut", "Final Game", "Player Id", "bbref Id")
    }

    override fun cells(): List<String> {
        return listOf(name, born, debut, finalgame, playerId, bbrefid)
    }
}

// TODO move these into a file for queries
val playerNamesByLength = """
    SELECT
        COALESCE(namegiven, 'unknown') || ' ' || COALESCE(namelast, 'unknown') as name,
        COALESCE(birthyear, 0) || '-' || COALESCE(birthmonth, 0) || '-' || COALESCE(birthday, 0) as born,
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
        COALESCE(birthyear, 0) || '-' || COALESCE(birthmonth, 0) || '-' || COALESCE(birthday, 0) as born,
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
                    // TODO what if, say, just the year is known:  '0-0-1888', e.g. ?
                    if (rs.getString("born") == "0-0-0") { "unknown" } else { rs.getString("born") },
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
