package org.aarbizu.baseballDatabankFrontend.records

import java.sql.ResultSet
import kweb.ElementCreator
import kweb.TrElement
import kweb.a
import kweb.new
import kweb.td

abstract class TableRecord {
    abstract fun headers(): List<String>
    abstract fun cells(): List<String>
    abstract fun render(tr: ElementCreator<TrElement>)
}

data class PlayerBasic(
    val name: String,
    val born: String,
    val debut: String,
    val finalgame: String
) : TableRecord() {

    override fun headers() = listOf("Name", "Born", "Debut", "Final Game")

    override fun cells() = listOf(name, born, debut, finalgame)

    override fun render(tr: ElementCreator<TrElement>) {
        cells().forEach { cell ->
            tr.td().text(cell)
        }
    }

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

    override fun headers() = listOf("Name", "Born", "Debut", "Final Game", "Player Id", "bbref")

    override fun cells() = listOf(name, born, debut, finalgame, playerId, bbrefid)

    override fun render(tr: ElementCreator<TrElement>) {
        cells().forEach { cell ->
            when (cell) {
                bbrefid -> tr.td().new {
                    a(mapOf("target" to "_blank"), href = bbrefid).text(playerId)
                }
                else -> tr.td().text(cell)
            }
        }
    }

    companion object {
        private const val bbrefUri = "https://www.baseball-reference.com/players"
        private const val bbrefSuffix = ".shtml"
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
                        decorateBbrefId(rs.getString("bbrefid"))
                    )
                )
            }
            records
        }

        fun decorateBbrefId(bbrefid: String): String {
            return "$bbrefUri/${bbrefid[0]}/$bbrefid$bbrefSuffix"
        }
    }
}
