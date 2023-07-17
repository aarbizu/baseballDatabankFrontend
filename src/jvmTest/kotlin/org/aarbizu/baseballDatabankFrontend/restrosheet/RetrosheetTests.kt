package org.aarbizu.baseballDatabankFrontend.restrosheet

import com.google.common.truth.Truth.assertThat
import org.aarbizu.baseballDatabankFrontend.retrosheet.GameLogs
import org.aarbizu.baseballDatabankFrontend.retrosheet.TeamInfo
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileInputStream

const val RESOURCES = "src/commonMain/resources"

class RetrosheetTests {

    @Test
    fun `read archive resource`() {
        val stream = FileInputStream("$RESOURCES/retrosheet/gl1871_2022.zip")
        assertThat(stream).isNotNull()
    }

    @Test
    fun `getting game log contents`() {
        val gl = GameLogs()
        val gameLog1989Csv = gl.getGameLogFromArchive("1989") {
            FileInputStream("$RESOURCES/retrosheet/gl1871_2022.zip")
        }

        assertThat(gameLog1989Csv.size).isGreaterThan(1)
        gameLog1989Csv.filter { it.isNotEmpty() }
            .map {
                it.split(",")
            }.map {
                "${it[0]}: ${it[3]} (${it[4]}) ${it[9]} at ${it[6]} (${it[7]}) ${it[10]}"
            }.also { println(it) }
    }

    @Test
    fun `reading historical team info`() {
        val fileUrl = File("$RESOURCES/retrosheet/team-abbreviations.csv").toURI().toURL()
        val teamInfoMap = TeamInfo().readHistoricalTeamInfo { fileUrl }

        assertThat(teamInfoMap["SFN"]!!.name).isEqualTo("Giants")
        assertThat(teamInfoMap["SFN"]!!.from).isEqualTo("1958")
    }

    @Test
    fun `getting game logs for given year`() {
        val fileUrl = File("$RESOURCES/retrosheet/team-abbreviations.csv").toURI().toURL()
        TeamInfo.teamInfoMap = TeamInfo().readHistoricalTeamInfo { fileUrl }
        val gl = GameLogs { FileInputStream("$RESOURCES/retrosheet/gl1871_2022.zip") }
        val gameLogs = gl.getGameLogs("1989")
        assertThat(gameLogs.size).isGreaterThan(1)

        println(gameLogs[0].toString())
    }
}