package org.aarbizu.baseballDatabankFrontend

import com.google.common.truth.Truth.assertThat
import io.ktor.network.util.DefaultDatagramByteBufferPool
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.aarbizu.baseballDatabankFrontend.db.DB
import org.aarbizu.baseballDatabankFrontend.db.DBProvider
import org.aarbizu.baseballDatabankFrontend.db.DataLoader
import org.aarbizu.baseballDatabankFrontend.records.TableRecord
import org.h2.jdbcx.JdbcConnectionPool
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import util.createTableSql
import util.upsertPlayerSql
import java.io.File

@ExtendWith(MockKExtension::class)
class NewDbTest {

    @MockK
    lateinit var dbmock: DBProvider

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

        connPool.dispose();
    }

    @Test
    fun `try to import bbdb csv`() {
        val currentDrivers = System.getProperty("jdbc.drivers") + ":org.h2.Driver"
        System.setProperty("jdbc.drivers", currentDrivers)
        val jdbcUrl = "jdbc:h2:mem:stats;DB_CLOSE_DELAY=-1"
        val connPool = JdbcConnectionPool.create(jdbcUrl, "stats", "stats")
        val fileOne = File("src/main/resources/csv/core/AllstarFull.csv")

        if (fileOne.canRead()) {
            val simplename = fileOne.nameWithoutExtension
            connPool.connection.use {
                it.createStatement().use { stmt ->
                    stmt.execute(
                        """
                            DROP TABLE IF EXISTS $simplename;
                            CREATE TABLE $simplename(
                                playerId text,
                                yearId int default null,
                                gameNum int DEFAULT null,
                                gameID text,
                                teamID text,
                                lgID text,
                                GP int DEFAULT null,
                                startingPos int DEFAULT null
                            ) AS 
                            SELECT * FROM CSVREAD('${fileOne.absolutePath}');
                        """.trimIndent()
                    )
                }
            }

            connPool.connection.use {
                it.createStatement().use { stmt ->
                    val rs = stmt.executeQuery("""
                        SELECT COUNT(*) from ${fileOne.nameWithoutExtension}
                    """.trimIndent())

                    rs.next()
                    val rows = rs.getInt(1)
                    assertThat(rows).isEqualTo(5454)
                }
            }
        }
    }

    @Test
    fun `list all csv files`() {
        var fileCount = 0
        val expectedFileCount = 28 // as of 4/3/2022, when copied from baseballdatabank repo
        DataLoader(dbmock).getCsvFiles().forEach {
            assertThat(it.isFile && it.extension == "csv")
            assertThat(it.canRead())
            fileCount += 1
        }
        assertThat(fileCount).isEqualTo(expectedFileCount)
    }

    @Test
    fun `test initializing db`() {
        val dataLoader = DataLoader(DB)
        dataLoader.loadAllFiles()
        dataLoader.buildIndexes()
        DB.getConnection().use {
            it.createStatement().use { stmt ->
                val rs = stmt.executeQuery(
                    """
                    SELECT COUNT(*) from people
                """.trimIndent()
                )
                rs.next()
                val playerCount = rs.getInt(1)
                assertThat(playerCount).isEqualTo(20370)
            }
        }
    }
}
