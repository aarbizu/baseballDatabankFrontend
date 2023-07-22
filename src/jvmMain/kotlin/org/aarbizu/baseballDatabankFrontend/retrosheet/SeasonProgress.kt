package org.aarbizu.baseballDatabankFrontend.retrosheet

import java.time.LocalDate

/**
 * Take a game log and produce a day-by-day tally of wins and losses per team
 */
class SeasonProgress {

    fun teamByTeamDailyResults(results: List<SimpleGameLog>): List<Standings> {
        val date = LocalDate.parse(results.first().date, logDateFormat)
        val baseStandings = initialStandings(MLBTeams.of(date.year))
        return results
            .groupBy { it.date }
            .toSortedMap()
            .mapValues { Standings.of(it.value) }
            .values
            .runningFold(baseStandings) { acc, standings -> acc + standings }
    }
}

