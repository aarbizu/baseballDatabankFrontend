package org.aarbizu.baseballDatabankFrontend.db

import com.google.common.base.Stopwatch
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import org.aarbizu.baseballDatabankFrontend.config.dbPassword
import org.aarbizu.baseballDatabankFrontend.config.dbUrl
import org.aarbizu.baseballDatabankFrontend.config.dbUser
import org.aarbizu.baseballDatabankFrontend.query.Bind
import org.aarbizu.baseballDatabankFrontend.query.IntBind
import org.aarbizu.baseballDatabankFrontend.query.StrBind
import org.aarbizu.baseballDatabankFrontend.query.playerLastNameSubstring
import org.aarbizu.baseballDatabankFrontend.query.playerNameRegex
import org.aarbizu.baseballDatabankFrontend.records.Player
import org.aarbizu.baseballDatabankFrontend.records.PlayerBasic
import org.aarbizu.baseballDatabankFrontend.records.TableRecord
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("QueryEngine")

object DB {
    val conn by lazy {
        DriverManager.getConnection(dbUrl, dbUser, dbPassword)
    }
}

class QueryEngine {

    companion object DbDriver {
        fun query(
            statement: PreparedStatement,
            extractor: (rs: ResultSet) -> List<TableRecord>
        ): List<TableRecord> {
            val timer = Stopwatch.createStarted()
            val rs = statement.executeQuery()
            timer.reset()
            logger.info("exec query: $timer")
            timer.start()
            val records: List<TableRecord> = extractor.invoke(rs)
            logger.info("extract results: $timer")
            timer.stop()
            return records
        }

        fun statement(queryTemplate: String, binds: List<Bind<*>>): PreparedStatement {
            val stmt = DB.conn.prepareStatement(queryTemplate)
            var paramIndex = 1
            binds.forEach {
                when (it::class) {
                    IntBind::class -> stmt.setInt(paramIndex++, it.value as Int)
                    StrBind::class -> stmt.setString(paramIndex++, it.value as String)
                    else -> {
                        logger.info("unknown type, ${it::class} from $it")
                    }
                }
            }
            return stmt
        }
    }

    fun playerNameSearch(nameSubstring: String): List<TableRecord> {
        return query(
            statement(
                playerLastNameSubstring,
                listOf(
                    StrBind(
                        "lnameSubstr",
                        nameSubstring
                    )
                )
            ),
            PlayerBasic.extract
        )
    }

    fun playerNamesByLength(length: String): List<TableRecord> {
        return query(
            statement(
                org.aarbizu.baseballDatabankFrontend.query.playerNamesByLength,
                listOf(
                    IntBind(
                        "lnameLength",
                        length.toInt()
                    )
                )
            ),
            Player.extract
        )
    }

    fun playerNameRegexSearch(regex: String, matchFirst: Boolean, matchLast: Boolean, caseSensitive: Boolean): List<TableRecord> {
        return query(
            statement(
                playerNameRegex(
                    matchFirst,
                    matchLast,
                    caseSensitive
                ),
                listOf(StrBind("nameRegex", regex))
            ),
            PlayerBasic.extract
        )
    }
}
