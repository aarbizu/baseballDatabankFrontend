package org.aarbizu.baseballDatabankFrontend.records

import java.sql.ResultSet

abstract class TableRecord {
    abstract fun headers(): List<String>
    abstract fun cells(): List<String>
}

data class PlayerBasic(
    val name: String,
    val born: String,
    val debut: String,
    val finalgame: String
) : TableRecord() {

    override fun headers() = listOf("Name", "Born", "Debut", "Final Game")

    override fun cells() = listOf(name, born, debut, finalgame)

    companion object {
        val extract: (rs: ResultSet) -> List<TableRecord> = { rs ->
            val records = mutableListOf<PlayerBasic>()
            while (rs.next()) {
                records.add(
                    PlayerBasic(
                        rs.getString("name"),
                        rs.getString("born"),
                        rs.getString("debut"),
                        rs.getString("finalgame")
                    )
                )
            }
            records
        }
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

    override fun headers() = listOf("Name", "Born", "Debut", "Final Game", "Player Id", "bbref Id")

    override fun cells() = listOf(name, born, debut, finalgame, playerId, bbrefid)

    companion object {
        val extract: (rs: ResultSet) -> List<TableRecord> = { rs ->
            val records = mutableListOf<Player>()
            while (rs.next()) {
                records.add(
                    Player(
                        rs.getString("name"),
                        rs.getString("born"),
                        rs.getString("debut"),
                        rs.getString("finalgame"),
                        rs.getString("playerid"),
                        rs.getString("bbrefid")
                    )
                )
            }
            records
        }
    }
}
