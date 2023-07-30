package org.aarbizu.baseballDatabankFrontend.retrosheet

import com.google.common.truth.Truth.assertThat
import jetbrains.datalore.plot.PlotSvgExport
import org.jetbrains.letsPlot.geom.geomDensity
import org.jetbrains.letsPlot.geom.geomLabel
import org.jetbrains.letsPlot.geom.geomStep
import org.jetbrains.letsPlot.ggsize
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.intern.toSpec
import org.jetbrains.letsPlot.label.ggtitle
import org.jetbrains.letsPlot.label.ylab
import org.jetbrains.letsPlot.letsPlot
import org.junit.jupiter.api.Test
import java.awt.Desktop
import java.io.File
import java.io.FileInputStream
import java.time.LocalDate

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
            }//.also { println(it) }
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
        val gameLogs = getYearLog("1989")
        assertThat(gameLogs.size).isGreaterThan(1)
        println(gameLogs[0].toString())
    }

    @Test
    fun `get standings from game log for given year`() {
        val log1901 = getYearLog("1901")

        val standings1901 = Standings.of(log1901)
        assertThat(standings1901).isNotNull()
        assertThat(standings1901.teamRecordsByDivision.size).isEqualTo(2)
        assertThat(standings1901.teamRecordsByDivision.keys).isEqualTo(setOf(AL1901, NL1901))
        assertThat(standings1901.teamRecordsByDivision[AL1901]!!.size).isEqualTo(8)
        assertThat(standings1901.teamRecordsByDivision[NL1901]!!.size).isEqualTo(8)
    }

    @Test
    fun `get standings for 1969 - first season with East and West divisions`() {
        val log1969 = getYearLog("1969")

        val standings1969 = Standings.of(log1969)
        assertThat(standings1969).isNotNull()
        assertThat(standings1969.teamRecordsByDivision.size).isEqualTo(4)
        assertThat(standings1969.teamRecordsByDivision.keys).isEqualTo(setOf(ALE1969, ALW1969, NLE1969, NLW1969))
        assertThat(standings1969.teamRecordsByDivision[ALE1969]!!.size).isEqualTo(6)
        assertThat(standings1969.teamRecordsByDivision[ALW1969]!!.size).isEqualTo(6)
        assertThat(standings1969.teamRecordsByDivision[NLE1969]!!.size).isEqualTo(6)
        assertThat(standings1969.teamRecordsByDivision[NLW1969]!!.size).isEqualTo(6)
    }

    @Test
    fun `get 1989 standings`() {
        val log1989 = getYearLog("1989")
        val standings1989 = Standings.of(log1989)
        assertThat(standings1989).isNotNull()
        assertThat(standings1989.teamRecordsByDivision.size).isEqualTo(4)
        assertThat(standings1989.teamRecordsByDivision.keys).isEqualTo(setOf(ALE1977, ALW1977, NLE1969, NLW1969))
        assertThat(standings1989.teamRecordsByDivision[ALE1977]!!.size).isEqualTo(7)
        assertThat(standings1989.teamRecordsByDivision[ALW1977]!!.size).isEqualTo(7)
        assertThat(standings1989.teamRecordsByDivision[NLE1969]!!.size).isEqualTo(6)
        assertThat(standings1989.teamRecordsByDivision[NLW1969]!!.size).isEqualTo(6)
    }

    @Test
    fun `get 1994 standings - first with three div`() {
        val log1994 = getYearLog("1994")
        val standings1994 = Standings.of(log1994)
        assertThat(standings1994).isNotNull()
        assertThat(standings1994.teamRecordsByDivision.size).isEqualTo(6)
        assertThat(standings1994.teamRecordsByDivision.keys).isEqualTo(setOf(ALE1994, ALW1994, ALC1994, NLE1994, NLW1994, NLC1994))
        assertThat(standings1994.teamRecordsByDivision[ALE1994]!!.size).isEqualTo(5)
        assertThat(standings1994.teamRecordsByDivision[ALC1994]!!.size).isEqualTo(5)
        assertThat(standings1994.teamRecordsByDivision[ALW1994]!!.size).isEqualTo(4)
        assertThat(standings1994.teamRecordsByDivision[NLE1994]!!.size).isEqualTo(5)
        assertThat(standings1994.teamRecordsByDivision[NLC1994]!!.size).isEqualTo(5)
        assertThat(standings1994.teamRecordsByDivision[NLW1994]!!.size).isEqualTo(4)
    }

    @Test
    fun `get 2013 standings - hou moves to ALW`() {
        val log2013 = getYearLog("2013")
        val standings2013 = Standings.of(log2013)
        assertThat(standings2013).isNotNull()
        assertThat(standings2013.teamRecordsByDivision.size).isEqualTo(6)
        assertThat(standings2013.teamRecordsByDivision.keys).isEqualTo(setOf(ALE1998, ALW2013, ALC1998, NLE2012, NLW1998, NLC2013))
        assertThat(standings2013.teamRecordsByDivision[ALE1998]!!.size).isEqualTo(5)
        assertThat(standings2013.teamRecordsByDivision[ALW2013]!!.size).isEqualTo(5)
        assertThat(standings2013.teamRecordsByDivision[ALC1998]!!.size).isEqualTo(5)
        assertThat(standings2013.teamRecordsByDivision[NLE2012]!!.size).isEqualTo(5)
        assertThat(standings2013.teamRecordsByDivision[NLW1998]!!.size).isEqualTo(5)
        assertThat(standings2013.teamRecordsByDivision[NLC2013]!!.size).isEqualTo(5)
    }

    @Test
    fun `get 2021 standings`() {
        val log2021 = getYearLog("2021")
        val standings2021 = Standings.of(log2021)
        assertThat(standings2021).isNotNull()
        assertThat(standings2021.teamRecordsByDivision.size).isEqualTo(6)
        assertThat(standings2021.teamRecordsByDivision.keys).isEqualTo(setOf(ALE1998, ALW2013, ALC1998, NLE2012, NLW1998, NLC2013))
        assertThat(standings2021.teamRecordsByDivision[ALE1998]!!.size).isEqualTo(5)
        assertThat(standings2021.teamRecordsByDivision[ALW2013]!!.size).isEqualTo(5)
        assertThat(standings2021.teamRecordsByDivision[ALC1998]!!.size).isEqualTo(5)
        assertThat(standings2021.teamRecordsByDivision[NLE2012]!!.size).isEqualTo(5)
        assertThat(standings2021.teamRecordsByDivision[NLW1998]!!.size).isEqualTo(5)
        assertThat(standings2021.teamRecordsByDivision[NLC2013]!!.size).isEqualTo(5)

        println("division name -> ${divToName(standings2021.teamRecordsByDivision.keys.first())}")
        println(standings2021)
        val summaries = standings2021.summaries()
        assertThat(summaries.keys.size).isEqualTo(6)
    }

    @Test
    fun `standings incrementing`() {
        val initialStandings = Standings(mutableMapOf(
            NL1901 to mutableListOf(TeamRecord("NY1", w = 1, l = 1, t = 0), TeamRecord("PIT", 0, 0 , 0))
        ))

        val incremental = Standings(mutableMapOf(
            NL1901 to mutableListOf(TeamRecord("NY1", w = 1, l = 1, t = 0), TeamRecord("PIT", w = 1, l =  1, t =  0))
        ))

        val summed = initialStandings + incremental
        assertThat(summed.teamRecordsByDivision[NL1901]).hasSize(2)
        assertThat(summed.teamRecordsByDivision[NL1901]!!.first { it.team == "NY1" }).isEqualTo(TeamRecord("NY1", 2, 2, 0))
        assertThat(summed.teamRecordsByDivision[NL1901]!!.first { it.team == "PIT" }).isEqualTo(TeamRecord("PIT", 1, 1, 0))
    }

    @Test
    fun dayByDayResults() {
        val seasonProgress = SeasonProgress()
        val dayByDay = seasonProgress.teamByTeamDailyResults(getYearLog("1901"))

        assertThat(dayByDay.size).isGreaterThan(0)

        val (dates, pct) = dayByDay["BSN"]
            ?.filterNot { it.first.isEmpty() }
            ?.map {
                val date = LocalDate.parse(it.first, logDateFormat)
                Pair(date.format(graphDateFormat), it.second)
            }
            ?.unzip()!!

        val (_, pct2) = dayByDay["PIT"]
            ?.filterNot { it.first.isEmpty() }
            ?.map {
                val date = LocalDate.parse(it.first, logDateFormat)
                Pair(date.format(graphDateFormat), it.second)
            }
            ?.unzip()!!

        val data = mapOf(
            "1901" to dates,
            "PIT" to pct2,
            "BSN" to pct
        )

        val plot = letsPlot(data) +
                geomStep(color = "blue") { x = "1901"; y = "BSN" } +
                geomLabel(data = mapOf("BSN" to listOf("BSN")), fontface = "bold", color = "blue",
                    x = data["1901"]?.lastIndex!! - 3, y = data["BSN"]?.last() as Double) { label = "BSN" } +
                geomStep(color = "red") { x = "1901"; y = "PIT" } +
                geomLabel(data = mapOf("PIT" to listOf("PIT")), fontface = "bold", color = "red",
                    x = data["1901"]?.lastIndex!! - 3, y =  data["PIT"]?.last() as Double
                ) { label = "PIT" }+
                ggsize(width = 1000, height = 625) +
                ggtitle("foo", "subfoo") +
                ylab("winning pct")

        val content = PlotSvgExport.buildSvgImageFromRawSpecs(plot.toSpec())
//        val content = PlotHtmlExport.buildHtmlFromRawSpecs(plot.toSpec(), scriptUrl(VersionChecker.letsPlotJsVersion))

        val dir = File(System.getProperty("user.dir"), "BSN-1901-day-by-day")
        dir.mkdir()
        val file = File(dir.canonicalPath, "test-BSN-1901-plot.html")
        file.createNewFile()
        file.writeText(content)

        Desktop.getDesktop().browse(file.toURI())
    }

    @Test
    fun testPlotFunction() {
        val seasonProgress = SeasonProgress()
        val dayByDay = seasonProgress.teamByTeamDailyResults(getYearLog("1901"))

        assertThat(dayByDay.size).isGreaterThan(0)

        val plot = seasonProgress.plot("1901", dayByDay, "NL")
        localBrowserPlot(plot, "1901nl")

        val plot2 = seasonProgress.plot("1901", dayByDay, "AL")
        localBrowserPlot(plot2, "1901al")
    }

    private fun localBrowserPlot(plot: Plot, name: String) {
        val content = PlotSvgExport.buildSvgImageFromRawSpecs(plot.toSpec())
//        val content = PlotHtmlExport.buildHtmlFromRawSpecs(plot.toSpec(), scriptUrl(VersionChecker.letsPlotJsVersion))

        val dir = File(System.getProperty("user.dir"), name)
        dir.mkdir()
        val file = File(dir.canonicalPath, "${name}plot.html")
        file.createNewFile()
        file.writeText(content)

        Desktop.getDesktop().browse(file.toURI())
    }

    @Test
    fun generateBaseStandings() {
        val map = MLB2013.leagues()
            .map { l -> l.divisions() }
            .flatten()
            .associateWith { d ->
                d.teams().map { t ->
                    TeamRecord(t, 0, 0, 0)
                }.toMutableList()
            }
        val baseStandings = Standings(map)
        assertThat(baseStandings).isNotNull()
    }

    @Test
    fun standingsDeepCopy() {
        val initialStandings = Standings(mutableMapOf(
            NL1901 to mutableListOf(TeamRecord("NY1", w = 1, l = 1, t = 0))
        ))

        val newStandings = initialStandings.deepCopy()
        assertThat(newStandings === initialStandings).isFalse()

    }

    @Test
    fun displayGraph() {
        val rand = java.util.Random()
        val n = 200
        val data = mapOf(
            "x" to List(n) { rand.nextGaussian() }
        )

        val plot = letsPlot(data) + geomDensity(
            color = "dark-green",
            fill = "green",
            alpha = .3,
            size = 2.0
        ) { x = "x" }
        val content = PlotSvgExport.buildSvgImageFromRawSpecs(plot.toSpec())

        val dir = File(System.getProperty("user.dir"), "lets-plot-images")
        dir.mkdir()
        val file = File(dir.canonicalPath, "my_plot.html")
        file.createNewFile()
        file.writeText(content)

        Desktop.getDesktop().browse(file.toURI())

    }

    private fun getYearLog(year: String): List<SimpleGameLog> {
        val fileUrl = File("$RESOURCES/retrosheet/team-abbreviations.csv").toURI().toURL()
        TeamInfo.teamInfoMap = TeamInfo().readHistoricalTeamInfo { fileUrl }
        val gl = GameLogs { FileInputStream("$RESOURCES/retrosheet/gl1871_2022.zip") }
        return gl.getGameLogs(year)
    }

}