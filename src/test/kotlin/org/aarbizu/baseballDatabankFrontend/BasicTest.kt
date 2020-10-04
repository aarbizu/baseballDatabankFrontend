package org.aarbizu.baseballDatabankFrontend

import com.google.common.truth.Truth.assertThat
import io.mockk.junit5.MockKExtension
import java.net.URI
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import org.aarbizu.baseballDatabankFrontend.config.getDbUri
import org.aarbizu.baseballDatabankFrontend.db.DBProvider
import org.aarbizu.baseballDatabankFrontend.db.DbConnectionParams
import org.aarbizu.baseballDatabankFrontend.db.QueryEngine
import org.aarbizu.baseballDatabankFrontend.db.StrBind
import org.aarbizu.baseballDatabankFrontend.records.TableRecord
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import util.setupTestDatabase
import util.upsertPlayer

@Testcontainers
@ExtendWith(MockKExtension::class)
class BasicTest {

    // BeforeAll method needs to be static (thanks Java)
    companion object {
        @Container
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer<Nothing>("postgres:13").withDatabaseName("stats")

        @JvmStatic
        @BeforeAll
        fun initTestDb() {
            setupTestDatabase(postgres)
        }
    }

    private fun getDbProvider(): DBProvider {
        return object : DBProvider {
            override fun getConnection(): Connection {
                return DriverManager.getConnection(postgres.jdbcUrl, postgres.username, postgres.password)
            }
        }
    }

    @Test
    fun `DbConnectionParams parses jdbc URIs properly`() {
        val urlEnvVar = getDbUri("username", "password", "ec2-54-86-57-171.compute-1.amazonaws.com", 5432, "path")
        val uri = URI(urlEnvVar)
        val dbConnParams = DbConnectionParams(uri)

        assertThat(dbConnParams.user).isEqualTo("username")
        assertThat(dbConnParams.password).isEqualTo("password")
        assertThat(dbConnParams.userinfo).isEqualTo("username:password")
        assertThat(dbConnParams.getJdbcUrl()).isEqualTo("jdbc:postgresql://ec2-54-86-57-171.compute-1.amazonaws.com:5432/path")
    }

    @Test
    fun `test db driver`() {
        upsertPlayer(postgres, "aaronha", 15000, 7)
        val testRecords = QueryEngine.DbDriver.query(
            getDbProvider(),
            "SELECT * from players where id = ?",
            listOf(StrBind("id", "aaronha")),
            TestPlayerRecord.extract
        )

        assertThat(testRecords.size).isEqualTo(1)
        assertThat(testRecords[0].cells()[0]).isEqualTo("aaronha")
    }

    @Test
    fun `simple connection to test container`() {
        DriverManager.getConnection(postgres.jdbcUrl, postgres.username, postgres.password).use {
            conn ->
                conn.createStatement().use { stmt ->
                    stmt.executeQuery("SELECT 1").use { resultSet ->
                        resultSet.next()
                        assertThat(resultSet.getInt(1)).isEqualTo(1)
                    }
                }
        }
    }

    data class TestPlayerRecord(val id: String, val games: Int, val pos: Int) : TableRecord() {
        override fun headers() = listOf("id", "games", "pos")
        override fun cells() = listOf(id, games.toString(), pos.toString())
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
}
