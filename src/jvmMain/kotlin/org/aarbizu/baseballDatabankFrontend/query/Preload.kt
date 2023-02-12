package org.aarbizu.baseballDatabankFrontend.query

import com.google.common.base.Stopwatch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import org.aarbizu.baseballDatabankFrontend.BaseballRecord
import org.aarbizu.baseballDatabankFrontend.NamesSortedByLengthParam
import org.aarbizu.baseballDatabankFrontend.OffenseStats
import org.aarbizu.baseballDatabankFrontend.PitchingStats
import org.aarbizu.baseballDatabankFrontend.SimplePlayerRecord
import java.util.logging.Logger

data class PreloadedResults(
    val minMaxValues: BaseballRecord,
    val orderedByLastNameLen: List<BaseballRecord>,
    val orderedByFirstNameLen: List<BaseballRecord>,
    val orderedByNameLen: List<BaseballRecord>,
    val orderedByFullNameLen: List<BaseballRecord>,
    val offenseStats: OffenseStats,
    val pitchingStats: PitchingStats,
) {
    companion object Instance {
        lateinit var preloads: PreloadedResults
    }
}

fun preloadQueries(q: QueryEngine): PreloadedResults {
    val timer = Stopwatch.createStarted()
    val preload =
        PreloadedResults(
            minMaxValues = q.minMaxNameLengthValues(),
            orderedByLastNameLen = q.orderedByLength("last"),
            orderedByFirstNameLen = q.orderedByLength("First"),
            orderedByNameLen = q.orderedByLength("FirstLast"),
            orderedByFullNameLen = q.orderedByLength("Full"),
            offenseStats = OffenseStats(OffenseStatsNames.values().map { it.name }),
            pitchingStats = PitchingStats(PitchingStatsNames.values().map { it.name }),
        )
    Logger.getLogger("Preload").info("preload queries: $timer")
    return preload
}

fun playerNamesSorted(params: NamesSortedByLengthParam): List<BaseballRecord> {
    val topCount = params.topN.toInt()
    val descending = params.descending.toBoolean()

    return when (params.type) {
        "last" -> getTopN(descending, topCount, PreloadedResults.preloads.orderedByLastNameLen)
        "first" -> getTopN(descending, topCount, PreloadedResults.preloads.orderedByFirstNameLen)
        "firstlast" -> getTopN(descending, topCount, PreloadedResults.preloads.orderedByNameLen)
        "full" -> getTopN(descending, topCount, PreloadedResults.preloads.orderedByFullNameLen)
        else -> emptyList()
    }
}

private fun getTopN(
    desc: Boolean,
    topN: Int,
    collection: List<BaseballRecord>,
): List<BaseballRecord> {
    val range =
        if (desc) {
            (0..topN)
        } else {
            ((collection.lastIndex - topN)..collection.lastIndex)
        }
    val retVal = if (desc) collection.slice(range) else collection.slice(range).reversed()
    return retVal.filter { (it as SimplePlayerRecord).first.isNotEmpty() }
}

fun toJsonArray(strs: List<String>): JsonArray {
    return buildJsonArray {
        strs.map { add(it) }
    }
}
