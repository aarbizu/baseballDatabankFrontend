package org.aarbizu.baseballDatabankFrontend.query

import com.google.common.base.Stopwatch
import org.aarbizu.baseballDatabankFrontend.BasePlayer
import org.aarbizu.baseballDatabankFrontend.BaseballRecord
import org.aarbizu.baseballDatabankFrontend.MinMaxValues
import org.aarbizu.baseballDatabankFrontend.PlayerCareerStatRecord
import org.aarbizu.baseballDatabankFrontend.PlayerNameLengthParam
import org.aarbizu.baseballDatabankFrontend.PlayerNameSearchParam
import org.aarbizu.baseballDatabankFrontend.PlayerSeasonStatRecord
import org.aarbizu.baseballDatabankFrontend.SimplePlayerRecord
import org.aarbizu.baseballDatabankFrontend.StatParam
import org.aarbizu.baseballDatabankFrontend.db.DBProvider
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

object NoneDbProvider : DBProvider {
    override fun getConnection(): Connection {
        // no op
        throw UnsupportedOperationException("no db ready")
    }

    override fun stats() { /*no op*/ }
}

object NoneQueryEngine : QueryEngine(NoneDbProvider)

open class QueryEngine(private val dbProvider: DBProvider) {
    companion object DbDriver {
        fun <T : BaseballRecord> query(
            dbProvider: DBProvider,
            queryTemplate: String,
            binds: List<Bind<*>>,
            extractor: (ResultSet) -> List<T>,
        ): List<T> {
            val logger = LoggerFactory.getLogger("QueryEngine")
            val timer = Stopwatch.createStarted()
            var records: List<T>
            // TODO -- preempt db conn usages with a cache based on hash of binds per queryTemplate
            dbProvider.getConnection().use {
                val rs = statement(it, queryTemplate, binds).executeQuery()
                logger.info("exec query: $timer")
                timer.reset()
                timer.start()
                records = extractor.invoke(rs)
                logger.info("extract results: $timer")
            }
            dbProvider.stats()
            return records
        }

        private fun statement(
            conn: Connection,
            queryTemplate: String,
            binds: List<Bind<*>>,
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

    fun playerNameSearch(params: PlayerNameSearchParam) =
        query(
            dbProvider,
            playerNameRegexSql(params.matchFirstName, params.matchLastName, params.caseSensitive),
            listOf(StrBind("nameRegex", params.nameSearchString)),
            simplePlayerRecordExtractor,
        )

    fun playerNamesByLength(params: PlayerNameLengthParam) =
        query(
            dbProvider,
            playerNameLengthSql(params.nameOption),
            listOf(IntBind("lnameLength", params.nameLength.toInt())),
            simplePlayerRecordExtractor,
        )

    fun singleSeasonHrTotals(firstOnly: Boolean = true) =
        query(
            dbProvider,
            singleSeasonHrTotalsSql(firstOnly),
            emptyList(),
            playerSeasonStatRecordExtractor,
        )

    fun offenseStatLeaders(hittingStatParam: StatParam) =
        query(
            dbProvider,
            careerStatleader(hittingStatParam.stat, "BATTING"),
            emptyList(),
            careerStatLeaders,
        )

    fun pitchingStatLeaders(pitchingStatParam: StatParam) =
        query(
            dbProvider,
            careerStatleader(pitchingStatParam.stat, "PITCHING"),
            emptyList(),
            careerStatLeaders,
        )

    fun minMaxNameLengthValues(): BaseballRecord {
        return query(dbProvider, minMaxNameLengthsSql, emptyList(), minMaxNameLengthsExtract)[0]
    }

    fun orderedByLength(nameOption: String): List<BaseballRecord> {
        return query(
            dbProvider,
            orderedByLengthSql(nameOption),
            emptyList(),
            simplePlayerRecordExtractor, // some OLD players are listed as name = 'unknown'
        )
    }

    fun allPlayerNames(): List<BasePlayer> {
        return query(
            dbProvider,
            namesAndIdsSql(),
            emptyList(),
            extractNameAndId,
        )
    }

    private val minMaxNameLengthsExtract: (ResultSet) -> List<MinMaxValues> = {
        it.next()
        listOf(
            MinMaxValues(
                minFirstName = it.getString("minFName"),
                maxFirstName = it.getString("maxFName"),
                minLastName = it.getString("minLName"),
                maxLastName = it.getString("maxLName"),
                minFirstAndLastName = it.getString("minName"),
                maxFirstAndLastName = it.getString("maxName"),
                minFullName = it.getString("minFull"),
                maxFullName = it.getString("maxFull"),
            ),
        )
    }

    private val simplePlayerRecordExtractor: (ResultSet) -> List<SimplePlayerRecord> = {
        val records = mutableListOf<SimplePlayerRecord>()
        while (it.next()) {
            records.add(
                SimplePlayerRecord(
                    first = it.getString("first"),
                    last = it.getString("last"),
                    given = it.getString("given"),
                    name = it.getString("name"),
                    born = it.getString("born"),
                    debut = it.getString("debut"),
                    finalGame = it.getString("finalgame"),
                    bbrefId = it.getString("bbrefid"),
                    playerId = it.getString("playerid"),
                    playerMgr = it.getString("playerManager"),
                ),
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
                    statName = it.getString("season_hr"),
                    // TODO need to make this able to retrieve an arbitrary stat
                ),
            )
        }
        records
    }

    private val careerStatLeaders: (ResultSet) -> List<PlayerCareerStatRecord> = {
        val records = mutableListOf<PlayerCareerStatRecord>()
        while (it.next()) {
            records.add(
                PlayerCareerStatRecord(
                    id = it.getString("playerid"),
                    name = it.getString("name"),
                    stat = it.getString("stat_total"),
                ),
            )
        }
        records
    }

    private val extractNameAndId: (ResultSet) -> List<BasePlayer> = {
        val records = mutableListOf<BasePlayer>()
        while (it.next()) {
            records.add(BasePlayer(it.getString("namefirst"), it.getString("namelast"), it.getString("playerid")))
        }
        records
    }
}
