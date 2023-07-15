package org.aarbizu.baseballDatabankFrontend.retrosheet

/**
 * Metadata for working with Retrosheet file data
 */
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
    val homeRuns: String
)

//TODO League structure over the years should go in here