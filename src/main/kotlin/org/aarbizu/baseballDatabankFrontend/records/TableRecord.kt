package org.aarbizu.baseballDatabankFrontend.records

import java.sql.ResultSet
import kotlinx.serialization.json.JsonPrimitive
import kweb.ElementCreator
import kweb.TrElement
import kweb.a
import kweb.new
import kweb.plugins.fomanticUI.fomantic
import kweb.span
import kweb.td

abstract class TableRecord {
    abstract fun headers(): List<String>
    abstract fun cells(): List<String>
    abstract fun render(tr: ElementCreator<TrElement>)
}

private const val bbrefUri = "https://www.baseball-reference.com/players"
private const val bbrefSuffix = ".shtml"

private fun decorateBbrefId(bbrefid: String): String {
    return "$bbrefUri/${bbrefid[0]}/$bbrefid$bbrefSuffix"
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

data class PlayerWithLinks(
    val name: String,
    val playerId: String,
    val bbrefId: String
) : TableRecord() {
    override fun headers() = listOf("Name")

    override fun cells() = listOf(name)

    override fun render(tr: ElementCreator<TrElement>) {

        cells().forEach { name ->
            tr.td().new {
                a(mapOf("target" to JsonPrimitive("_blank")), href = decorateBbrefId(bbrefId)).text(name)
                if (bbrefId == playerId) {
                    span(fomantic.ui.small.text).text(" [$bbrefId]")
                } else {
                    span(fomantic.ui.small.text).text(" [bbref: $bbrefId]")
                    span(fomantic.ui.small.text).text(" [db: $playerId]")
                }
            }
        }
    }

    companion object {
        val extract: (rs: ResultSet) -> List<TableRecord> = { rs ->
            val records = mutableListOf<PlayerWithLinks>()
            while (rs.next()) {
                records.add(
                    PlayerWithLinks(
                        rs.getString("name"),
                        rs.getString("bbrefid"),
                        rs.getString("playerid")
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
                    a(mapOf("target" to JsonPrimitive("_blank")), href = decorateBbrefId(bbrefid)).text(playerId)
                }
                else -> tr.td().text(cell)
            }
        }
    }

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

data class SinglePlayerStat(
    val name: String,
    val year: String,
    val value: Int,
    val statName: String
) : TableRecord() {
    override fun headers() = listOf("Name", "Year", statName)

    override fun cells() = listOf(name, year, value.toString())

    override fun render(tr: ElementCreator<TrElement>) {
        cells().forEach { tr.td().text(it) }
    }
}

fun singlePlayerStatExtract(colName: String, statDisplayName: String): (rs: ResultSet) -> List<TableRecord> {
    return { rs: ResultSet ->
        val records = mutableListOf<SinglePlayerStat>()
        while (rs.next()) {
            records.add(
                SinglePlayerStat(
                    rs.getString("year"),
                    rs.getString("playername"),
                    rs.getInt(colName),
                    statDisplayName
                )
            )
        }
        records
    }
}
