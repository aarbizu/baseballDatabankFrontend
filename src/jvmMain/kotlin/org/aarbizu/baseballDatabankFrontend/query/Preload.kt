package org.aarbizu.baseballDatabankFrontend.query

import com.google.common.base.Stopwatch
import org.aarbizu.baseballDatabankFrontend.BaseballRecord
import org.aarbizu.baseballDatabankFrontend.NamesSortedByLengthParam
import java.util.logging.Logger

data class PreloadedResults(
    val minMaxValues: BaseballRecord,
    val orderedByLastNameLen: List<BaseballRecord>,
    val orderedByFirstNameLen: List<BaseballRecord>,
    val orderedByNameLen: List<BaseballRecord>,
    val orderedByFullNameLen: List<BaseballRecord>
) {
    companion object Instance {
        lateinit var preloads: PreloadedResults
    }
}

fun preloadQueries(q: QueryEngine): PreloadedResults {
    val timer = Stopwatch.createStarted()
    val preload =
        PreloadedResults(
            q.minMaxNameLengthValues(),
            q.orderedByLength("last"),
            q.orderedByLength("First"),
            q.orderedByLength("FirstLast"),
            q.orderedByLength("Full"),
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
    collection: List<BaseballRecord>
): List<BaseballRecord> {
    val range =
        if (desc) {
            (0..topN)
        } else {
            ((collection.lastIndex - topN)..collection.lastIndex)
        }
    return collection.slice(range)
}
