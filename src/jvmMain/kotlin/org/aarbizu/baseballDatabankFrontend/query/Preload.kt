package org.aarbizu.baseballDatabankFrontend.query

import com.google.common.base.Stopwatch
import org.aarbizu.baseballDatabankFrontend.BaseballRecord
import java.util.logging.Logger

data class PreloadedResults(
    val minMaxValues: BaseballRecord,
    val orderedByLastNameLen: BaseballRecord,
    val orderedByFirstNameLen: BaseballRecord,
    val orderedByNameLen: BaseballRecord,
    val orderedByFullNameLen: BaseballRecord
)

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
