package org.aarbizu.baseballDatabankFrontend.db

import com.google.common.base.Stopwatch
import java.net.URI
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import org.aarbizu.baseballDatabankFrontend.config.dbUri
import org.aarbizu.baseballDatabankFrontend.query.playerLastNameSubstring
import org.aarbizu.baseballDatabankFrontend.query.playerNameRegex
import org.aarbizu.baseballDatabankFrontend.records.Player
import org.aarbizu.baseballDatabankFrontend.records.TableRecord
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("QueryEngine")

interface DBProvider {
    fun getConnection(): Connection
}

data class DbConnectionParams(val uri: URI) {
    val userinfo: String by lazy {
        uri.userInfo
    }

    val user: String by lazy {
        userinfo.split(":")[0]
    }

    val password: String by lazy {
        userinfo.split(":")[1]
    }

    fun getJdbcUrl() = "jdbc:postgresql://${uri.host}:${uri.port}${uri.path}"
}

object DB : DBProvider {
    private val connParams: DbConnectionParams by lazy {
        DbConnectionParams(dbUri)
    }

    private val conn: Connection by lazy {
        DriverManager.getConnection(connParams.getJdbcUrl(), connParams.user, connParams.password)
    }

    override fun getConnection() = conn
}

class QueryEngine(private val dbProvider: DBProvider) {
    companion object DbDriver {
        fun query(
            dbProvider: DBProvider,
            queryTemplate: String,
            binds: List<Bind<*>>,
            extractor: (rs: ResultSet) -> List<TableRecord>
        ): List<TableRecord> {
            val timer = Stopwatch.createStarted()
            val rs = statement(dbProvider, queryTemplate, binds).executeQuery()
            logger.info("exec query: $timer")
            timer.reset()
            timer.start()
            val records: List<TableRecord> = extractor.invoke(rs)
            logger.info("extract results: $timer")
            return records
        }

        private fun statement(dbProvider: DBProvider, queryTemplate: String, binds: List<Bind<*>>): PreparedStatement {
            val stmt = dbProvider.getConnection().prepareStatement(queryTemplate)
            var paramIndex = 1
            binds.forEach {
                when (it::class) {
                    IntBind::class -> stmt.setInt(paramIndex++, it.value as Int)
                    StrBind::class -> stmt.setString(paramIndex++, it.value as String)
                    else -> logger.info("unknown type, ${it::class} from $it")
                }
            }
            return stmt
        }
    }

    fun playerNameSearch(nameSubstring: String) = query(
        dbProvider,
        playerLastNameSubstring,
        listOf(
            StrBind(
                "lnameSubstr",
                nameSubstring
            )
        ),
        Player.extract
    )

    fun playerNamesByLength(length: String) = query(
        dbProvider,
        org.aarbizu.baseballDatabankFrontend.query.playerNamesByLength,
        listOf(
            IntBind(
                "lnameLength",
                length.toInt()
            )
        ),
        Player.extract
    )

    fun playerNameRegexSearch(regex: String, matchFirst: Boolean, matchLast: Boolean, caseSensitive: Boolean) = query(
        dbProvider,
        playerNameRegex(
            matchFirst,
            matchLast,
            caseSensitive
        ),
        listOf(StrBind("nameRegex", regex)),
        Player.extract
    )
}
