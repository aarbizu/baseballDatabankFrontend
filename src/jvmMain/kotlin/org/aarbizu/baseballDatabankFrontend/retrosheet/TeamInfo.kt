package org.aarbizu.baseballDatabankFrontend.retrosheet

import java.io.File
import java.net.URL

/**
 * Historical team info
 */
class TeamInfo {

    fun initializeMap() {
        teamInfoMap = readHistoricalTeamInfo(teamNameInfoProvider)
    }

    internal fun readHistoricalTeamInfo(teamInfo: () -> URL?): Map<String, Team> {
        return teamInfo.invoke()?.let {
            File(it.file).readLines(Charsets.UTF_8)
                .map { line ->
                    val fields = line.replace("\"", "").split(",")
                    Team(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5])
                }
                .associateBy { team -> team.abbrev }
        }.orEmpty()
    }

    companion object {
        val teamNameInfoProvider: () -> URL? = {
            object{ }.javaClass.getResource("team-abbreviations.csv")
        }

        var teamInfoMap: Map<String, Team> = emptyMap()
    }
}