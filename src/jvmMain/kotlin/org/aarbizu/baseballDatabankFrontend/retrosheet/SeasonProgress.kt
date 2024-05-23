package org.aarbizu.baseballDatabankFrontend.retrosheet

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import jetbrains.datalore.plot.PlotSvgExport
import org.jetbrains.letsPlot.geom.geomLabel
import org.jetbrains.letsPlot.geom.geomStep
import org.jetbrains.letsPlot.ggsize
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.intern.toSpec
import org.jetbrains.letsPlot.label.ggtitle
import org.jetbrains.letsPlot.label.ylab
import org.jetbrains.letsPlot.letsPlot
import java.time.LocalDate

/**
 * Gruvbox palette
 */
// val plotColors = listOf(
//    "#9d0006",
//    "#79740e",
//    "#b57614",
//    "#076678",
//    "#8f3f71",
//    "#427b58",
//    "#af3a03",
//    "#d65d0e",
//    "#fb4934",
//    "#83a598",
//    "#d3869b",
//    "#d79921",
//    "#458588",
//    "#cc241d",
//    "#fe8019",
//    "#3c3836",
//    "#b16286"
// )

/**
 * Solarized palette, then some generic colors
 */
val plotColors = listOf(
    "#b58900",
    "#cb4b16",
    "#dc322f",
    "#d33682",
    "#6c71c4",
    "#268bd2",
    "#2aa198",
    "#859900",
    "#586e76",
    "red",
    "blue",
    "green",
    "orange",
    "cyan",
    "black",
    "purple",
)

/**
 * Take a game log and produce a day-by-day tally of wins and losses per team
 */
class SeasonProgress {

    private val plotCache: LoadingCache<Pair<String, String>, String> = CacheBuilder.newBuilder()
        .build(
            CacheLoader.from { (year: String, division: String) ->
                innerPlotDayByDayStandings(year, division)
            },
        )

    fun plotDayByDayStandings(year: String, division: String): String {
        return plotCache.get(Pair(year, division))
    }

    private fun innerPlotDayByDayStandings(year: String, division: String): String {
        val plot = plotDayByDayStandingsHelper(year, division, GameLogs())
        return PlotSvgExport.buildSvgImageFromRawSpecs(plot.toSpec())
    }

    internal fun plotDayByDayStandingsHelper(year: String, division: String, gameLogs: GameLogs): Plot {
        val teamByTeamDailyResults = teamByTeamDailyResults(gameLogs.getGameLogs(year))
        return plot(year, division, teamByTeamDailyResults)
    }

    // TODO -- maybe refactor the List<> or the Pair<> into objects?
    internal fun teamByTeamDailyResults(results: List<SimpleGameLog>): Map<String, List<Pair<String, Double>>> {
        val progressiveStandings = standingsByDay(results)

        val teamSeasonProgress = mutableMapOf<String, MutableList<Pair<String, Double>>>()

        // generate a mapping of date to list of (team abbr, winning pct) pairs
        val mapValues = progressiveStandings
            .associateBy { it.asOf }
            .mapValues { standings -> standings.value.teamRecordsByDivision.flatMap { div -> div.value } }
            .mapValues { dateTeamWinningPct -> dateTeamWinningPct.value.associate { it.team to it.toDatum().second } }
            .mapValues { teamWinningPct ->
                teamWinningPct.value.flatMap { entry ->
                    listOf(Pair(entry.key, entry.value))
                }
            }

        // merge the map into list of (date,winning pct) by "team abbr" for graphing {Fe.g. "NY1" -> ((04011900,0.0), (04021900,0.5))}
        mapValues.forEach { entry ->
            entry.value.forEach { pair ->
                teamSeasonProgress.merge(pair.first, mutableListOf(Pair(entry.key, pair.second))) { old, new -> (old + new).toMutableList() }
            }
        }

        return teamSeasonProgress
    }

    private fun standingsByDay(results: List<SimpleGameLog>): List<Standings> {
        val date = LocalDate.parse(results.first().date, logDateFormat)
        val baseStandings = initialStandings(MLBTeams.of(date.year))

        // generate a Standings object for each day in the season's game logs
        return results
            .groupBy { result -> result.date }
            .toSortedMap()
            .mapValues { Standings.of(it.value, it.key) }
            .values
            .runningFold(baseStandings) { acc, standings -> acc + standings }
    }

    private fun plot(year: String, div: String, dailyResults: Map<String, List<Pair<String, Double>>>): Plot {
        require(div.isNotEmpty() && div.isNotBlank()) { "Invalid division" }
        require(year == dailyResults.entries.first().value.last().first.subSequence(0, 4)) { "$year doesn't match data" }
        val mlb = MLBTeams.of(year.toInt())
        val division = mlb.leagues().flatMap { it.divisions() }.firstOrNull { divToName(it) == div }
        requireNotNull(division)

        // pick any team to get the list of date values, given by [graphDateFormat]
        val (dates, _) = dateWPctPairs(dailyResults, mlb.leagues().first().divisions().first().teams().first())
        val data: MutableMap<String, List<Any>> = mutableMapOf(year to dates)

        division.teams().map {
            val (_, results) = dateWPctPairs(dailyResults, it)
            data[it] = results
        }

        val plot = letsPlot(data)
        val lastXIdx = data[year]?.lastIndex!! - 10

        val graphFeatures = division.teams().mapIndexed { index, team ->
            geomStep(color = plotColors[index]) { x = year; y = team } +
                geomLabel(
                    data = mapOf(team to listOf(team)),
                    fontface = "bold",
                    color = plotColors[index],
                    x = lastXIdx - (index * 10),
                    y = data[team]?.last() as Double,
                ) { label = team }
        }

        val teamList = division.toTeamNames().chunked(4).joinToString("\n") { it.joinToString(" : ") }

        return graphFeatures.foldRight(plot) { feature, p -> p + feature } +
            geomLabel(size = 5, x = data[year]?.size?.div(2), y = 0.10, label = teamList) +
            ggsize(width = 1000, height = 625) +
            ggtitle("Daily Standings", "$year - $div") +
            ylab("winning pct")
    }

    private fun dateWPctPairs(dailyResults: Map<String, List<Pair<String, Double>>>, team: String) =
        dailyResults[team]
            ?.filterNot { it.first.isEmpty() }
            ?.map {
                val date = LocalDate.parse(it.first, logDateFormat)
                Pair(date.format(graphDateFormat), it.second)
            }
            ?.unzip()!!
}
