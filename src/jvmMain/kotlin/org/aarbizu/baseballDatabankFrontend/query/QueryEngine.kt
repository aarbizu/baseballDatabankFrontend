package org.aarbizu.baseballDatabankFrontend.query

import com.google.common.base.Stopwatch
import org.aarbizu.baseballDatabankFrontend.BaseballRecord
import org.aarbizu.baseballDatabankFrontend.PlayerSeasonStatRecord
import org.aarbizu.baseballDatabankFrontend.SimplePlayerRecord
import org.aarbizu.baseballDatabankFrontend.db.DBProvider
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class QueryEngine(private val dbProvider: DBProvider) {
    companion object DbDriver {
        fun query(
            dbProvider: DBProvider,
            queryTemplate: String,
            binds: List<Bind<*>>,
            extractor: (ResultSet) -> List<BaseballRecord>
        ): List<BaseballRecord> {
            val logger = LoggerFactory.getLogger("QueryEngine")
            val timer = Stopwatch.createStarted()
            var records: List<BaseballRecord>
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

        private fun statement(
            conn: Connection,
            queryTemplate: String,
            binds: List<Bind<*>>
        ): PreparedStatement {
            val logger = LoggerFactory.getLogger("QueryEngine")
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

    fun playerNameSearch(nameSubstring: String) =
        query(
            dbProvider,
            playerLastNameSubstringSql,
            listOf(StrBind("lnameSubstr", "%${nameSubstring.lowercase()}%")),
            simplePlayerRecordExtractor
        )

    fun playerNamesByLength(length: String) =
        query(
            dbProvider,
            playerNamesByLengthSql,
            listOf(IntBind("lnameLength", length.toInt())),
            simplePlayerRecordExtractor
        )

    fun playerNameRegexSearch(
        regex: String,
        matchFirst: Boolean,
        matchLast: Boolean,
        caseSensitive: Boolean
    ) =
        query(
            dbProvider,
            playerNameRegexSql(matchFirst, matchLast, caseSensitive),
            listOf(StrBind("nameRegex", regex)),
            simplePlayerRecordExtractor
        )

    fun singleSeasonHrTotals(firstOnly: Boolean = true) =
        query(
            dbProvider,
            singleSeasonHrTotalsSql(firstOnly),
            emptyList(),
            playerSeasonStatRecordExtractor
        )

    private val simplePlayerRecordExtractor: (ResultSet) -> List<SimplePlayerRecord> = {
        val records = mutableListOf<SimplePlayerRecord>()
        while (it.next()) {
            records.add(
                SimplePlayerRecord(
                    name = it.getString("name"),
                    born = it.getString("born"),
                    debut = it.getString("debut"),
                    finalGame = it.getString("finalgame"),
                    bbrefId = it.getString("bbrefid"),
                    playerId = it.getString("playerid")
                )
            )
        }
        records
    }

    private val playerSeasonStatRecordExtractor: (ResultSet) -> List<PlayerSeasonStatRecord> = {
        val records = mutableListOf<PlayerSeasonStatRecord>()
        while (it.next()) {
            records.add(
                PlayerSeasonStatRecord(
                    year = it.getString("year"),
                    name = it.getString("name"),
                    statName =
                    it.getString(
                        "season_hr"
                    ), // TODO need to make this able to retrieve an arbitrary stat
                )
            )
        }
        records
    }
}
