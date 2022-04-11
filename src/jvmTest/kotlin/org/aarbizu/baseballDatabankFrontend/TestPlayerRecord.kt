package org.aarbizu.baseballDatabankFrontend

import kweb.ElementCreator
import kweb.TrElement
import org.aarbizu.baseballDatabankFrontend.records.TableRecord
import java.sql.ResultSet

data class TestPlayerRecord(val id: String, val games: Int, val pos: Int) : TableRecord() {
    override fun headers() = listOf("id", "games", "pos")
    override fun cells() = listOf(id, games.toString(), pos.toString())
    override fun render(tr: ElementCreator<TrElement>) {
        TODO("Not yet implemented")
    }

    companion object {
        val extract: (rs: ResultSet) -> List<TableRecord> = { rs ->
            val records = mutableListOf<TestPlayerRecord>()
            while (rs.next()) {
                records.add(
                    TestPlayerRecord(
                        rs.getString("id"),
                        rs.getInt("games"),
                        rs.getInt("pos")
                    )

                )
            }
            records
        }
    }
}