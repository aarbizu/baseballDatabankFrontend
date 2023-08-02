package org.aarbizu.baseballDatabankFrontend.retrosheet

import java.io.File
import java.io.InputStream

/**
 * Historical team info
 */

val RETROSHEET_PATH = "retrosheet${File.separator}team-abbreviations.csv"
class TeamInfo {

    fun initializeMap() {
        teamInfoMap = readHistoricalTeamInfo(teamNameInfoProvider)
    }

    internal fun readHistoricalTeamInfo(teamInfo: () -> InputStream?): Map<String, Team> {
        return teamInfo.invoke()?.let {
            it.bufferedReader().readLines()
                .map { line ->
                    val fields = line.replace("\"", "").split(",")
                    Team(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5])
                }
                .associateBy { team -> team.abbrev }
        }.orEmpty()
    }

    companion object {
        val teamNameInfoProvider: () -> InputStream? = {
            object { }.javaClass.getResourceAsStream("${File.separator}$RETROSHEET_PATH")
        }

        var teamInfoMap: Map<String, Team> = emptyMap()
    }
}
