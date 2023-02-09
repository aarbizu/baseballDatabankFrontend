package org.aarbizu.baseballDatabankFrontend

import java.sql.ResultSet

data class TestPlayerRecord(val id: String, val games: Int, val pos: Int) {
    companion object {
        val extract: (ResultSet) -> List<TestPlayerRecord> = { rs ->
            val records = mutableListOf<TestPlayerRecord>()
            while (rs.next()) {
                records.add(
                    TestPlayerRecord(rs.getString("id"), rs.getInt("games"), rs.getInt("pos")),
                )
            }
            records
        }
    }
}
