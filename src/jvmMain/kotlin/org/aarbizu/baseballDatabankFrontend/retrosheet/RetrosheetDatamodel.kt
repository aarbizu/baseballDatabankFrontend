package org.aarbizu.baseballDatabankFrontend.retrosheet

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Metadata for working with Retrosheet file data
 */
val logDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

data class Team(
    val abbrev: String,
    val league: String,
    val city: String,
    val name: String,
    val from: String,
    val to: String
)
data class SimpleGameLog(
    val date: String,
    val visitorTeam: String,
    val visitorLeague: String,
    val visitorRuns: String,
    val homeTeam: String,
    val homeLeague: String,
    val homeRuns: String,
) {
    override fun toString(): String = "${LocalDate.parse(date, logDateFormat)} $visitorTeam ($visitorLeague) $visitorRuns vs $homeTeam ($homeLeague) $homeRuns"
}


//TODO League structure over the years should go in here