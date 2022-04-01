package org.aarbizu.baseballDatabankFrontend

import com.google.common.truth.Truth.assertThat
import org.aarbizu.baseballDatabankFrontend.records.TableRecord
import org.h2.jdbcx.JdbcConnectionPool
import org.junit.jupiter.api.Test
import util.createTableSql
import util.upsertPlayerSql

class NewDbTest {

    @Test
    fun `try h2 in-mem db`() {
        val currentDrivers = System.getProperty("jdbc.drivers") + ":org.h2.Driver"
        System.setProperty("jdbc.drivers", currentDrivers)
        val jdbcUrl = "jdbc:h2:mem:stats;DB_CLOSE_DELAY=-1"
        var records: List<TableRecord>
        val connPool = JdbcConnectionPool.create(jdbcUrl, "stats", "stats")

        connPool.connection.use {
            it.createStatement().use { stmt ->
                stmt.execute(createTableSql)
            }
        }

        connPool.connection.use {
            it.createStatement().use { stmt ->
                stmt.execute(upsertPlayerSql("aaronha", 15000, 7))
            }
            it.commit()
        }

        connPool.connection.use {
            val stmt = it.prepareStatement("SELECT * from players where id = ?").apply {
                setString(1, "aaronha")
            }
            records = BasicTest.TestPlayerRecord.extract.invoke(stmt.executeQuery())
        }

        assertThat(records.lastIndex).isEqualTo(0)
    }

    @Test
    fun `try to import bbdb csv`() {
        TODO("write this test")
    }
}
