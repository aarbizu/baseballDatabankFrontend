package org.aarbizu.baseballDatabankFrontend.db

import com.google.common.base.Stopwatch
import org.aarbizu.baseballDatabankFrontend.query.playerLastNameSubstringSql
import org.aarbizu.baseballDatabankFrontend.query.playerNameRegexSql
import org.aarbizu.baseballDatabankFrontend.query.playerNamesByLengthSql
import org.aarbizu.baseballDatabankFrontend.query.singleSeasonHrTotalsSql
import org.aarbizu.baseballDatabankFrontend.records.Player
import org.aarbizu.baseballDatabankFrontend.records.PlayerWithLinks
import org.aarbizu.baseballDatabankFrontend.records.TableRecord
import org.aarbizu.baseballDatabankFrontend.records.singlePlayerStatExtract
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

private val logger = LoggerFactory.getLogger("QueryEngine")

class QueryEngine(private val dbProvider: DBProvider) {
    companion object DbDriver {
        fun query(
            dbProvider: DBProvider,
            queryTemplate: String,
            binds: List<Bind<*>>,
            extractor: (rs: ResultSet) -> List<TableRecord>
        ): List<TableRecord> {
            val timer = Stopwatch.createStarted()
            var records: List<TableRecord>
            dbProvider.getConnection().use {
                val rs = statement(it, queryTemplate, binds).executeQuery()
                logger.info("exec query: $timer")
                timer.reset()
                timer.start()
                // TODO -- caching here, based on a hash of binds and queryTemplate.
                records = extractor.invoke(rs)
                logger.info("extract results: $timer")
            }
            dbProvider.stats()
            return records
        }

        private fun statement(conn: Connection, queryTemplate: String, binds: List<Bind<*>>): PreparedStatement {
            val stmt = conn.prepareStatement(queryTemplate)
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
        playerLastNameSubstringSql,
        listOf(
            StrBind(
                "lnameSubstr",
                "%${nameSubstring.lowercase()}%"
            )
        ),
        Player.extract
    )

    fun playerNamesByLength(length: String) = query(
        dbProvider,
        playerNamesByLengthSql,
        listOf(
            IntBind(
                "lnameLength",
                length.toInt()
            )
        ),
        PlayerWithLinks.extract
    )

    fun playerNameRegexSearch(regex: String, matchFirst: Boolean, matchLast: Boolean, caseSensitive: Boolean) = query(
        dbProvider,
        playerNameRegexSql(
            matchFirst,
            matchLast,
            caseSensitive
        ),
        listOf(StrBind("nameRegex", regex)),
        Player.extract
    )

    fun singleSeasonHrTotals(firstOnly: Boolean = true) = query(
        dbProvider,
        singleSeasonHrTotalsSql(firstOnly),
        emptyList(),
        singlePlayerStatExtract("season_hr", "HR")
    )
}
