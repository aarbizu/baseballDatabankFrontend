package org.aarbizu.baseballDatabankFrontend.retrosheet

import java.lang.UnsupportedOperationException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Metadata for working with Retrosheet file data
 *
 * references:
 *
 *  -  [AL history](https://en.wikipedia.org/wiki/American_League)
 *  -  [NL history](https://en.wikipedia.org/wiki/National_League_(baseball))
 */
val logDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
val graphDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd")

data class Team(
    val abbrev: String,
    val league: String,
    val city: String,
    val name: String,
    val from: String,
    val to: String,
)

data class SimpleGameLog(
    val date: String,
    val visitorTeam: String,
    val visitorLeague: String,
    val visitorRuns: String,
    val visitor: String,
    val homeTeam: String,
    val homeLeague: String,
    val homeRuns: String,
    val home: String,
) {
    override fun toString(): String = "${LocalDate.parse(date, logDateFormat)} $visitorTeam ($visitorLeague) $visitorRuns vs $homeTeam ($homeLeague) $homeRuns"
}

data class TeamRecord(val team: String, var w: Int, var l: Int, var t: Int) {
    operator fun plus(inc: TeamRecord): TeamRecord {
        require(this.team == inc.team)
        this.w += inc.w
        this.l += inc.l
        this.t += inc.t
        return this.copy(w = (this.w + inc.w), l = (this.l + inc.l), t = (this.t + inc.t))
    }

    fun toDatum(): Pair<String, Double> {
        return Pair(team, if ((w + l + t == 0)) 0.0 else (w.toDouble() / (w + l + t).toDouble()))
    }

    override fun toString(): String = "$team - W$w L$l T$t"
}

fun initialStandings(mlbYear: MLB): Standings {
    return Standings(
        mlbYear.leagues()
            .map { l -> l.divisions() }
            .flatten()
            .associateWith { d ->
                d.teams().map { t ->
                    TeamRecord(t, 0, 0, 0)
                }.toMutableList()
            },
    )
}

fun modernDivisionList(): Map<String, List<String>> {
    return (1901..2022).map {
        Pair(it, MLBTeams.of(it))
    }.map { yearAndMlb ->
        Pair(
            yearAndMlb.first,
            yearAndMlb.second.leagues()
                .flatMap {
                    it.divisions()
                }.map {
                    val divName = it.javaClass.simpleName
                    divName.substring(0, divName.length - 4)
                },
        )
    }.associate {
        Pair(it.first.toString(), it.second)
    }
}

class Standings(val teamRecordsByDivision: Map<Division, List<TeamRecord>>, val asOf: String = "") {
    operator fun plus(inc: Standings): Standings {
        val toUpdate = deepCopy().teamRecordsByDivision

        for (d in toUpdate.keys) {
            if (inc.teamRecordsByDivision[d] != null) {
                for (rec in inc.teamRecordsByDivision[d]!!) {
                    with(toUpdate[d]?.first { it.team == rec.team }) { this?.plus(rec) ?: rec }
                }
            }
        }

        return Standings(toUpdate, inc.asOf)
    }

    override fun toString(): String {
        return teamRecordsByDivision.entries.joinToString(System.lineSeparator()) {
            "${divToName(it.key)}${System.lineSeparator()}${
                it.value
                    .sortedByDescending { rec -> rec.w }
                    .joinToString(System.lineSeparator()) { rec -> rec.toString() }
            }"
        }
    }

    fun summaries(): Map<Division, List<Pair<String, Double>>> {
        return teamRecordsByDivision.mapValues { (_, value) ->
            value.map { it.toDatum() }
        }
    }

    fun deepCopy(): Standings {
        val copy = mutableMapOf<Division, List<TeamRecord>>()
        teamRecordsByDivision.forEach { (k, v) ->
            val recordsCopy = mutableListOf<TeamRecord>()
            v.forEach { recordsCopy.add(it.copy()) }
            copy[k] = recordsCopy
        }
        return Standings(copy)
    }

    companion object {
        fun of(log: List<SimpleGameLog>, date: String = ""): Standings {
            val records = mutableMapOf<Division, MutableList<TeamRecord>>()
            val year = LocalDate.parse(log.first().date, logDateFormat).year
            val teams = MLBTeams.of(year)
            log.map { glog ->
                val homeDiv = teams.getDivision(glog.home)
                val visitorDiv = teams.getDivision(glog.visitor)
                if (homeDiv == visitorDiv) {
                    val divRecs = records.getOrDefault(homeDiv, mutableListOf())
                    updateRecord(divRecs, glog.homeTeam, winLoseOrTie(glog.homeRuns, glog.visitorRuns))
                    updateRecord(divRecs, glog.visitorTeam, winLoseOrTie(glog.visitorRuns, glog.homeRuns))
                    records[homeDiv!!] = divRecs
                } else {
                    val homeDivRecs = records.getOrDefault(homeDiv, mutableListOf())
                    val visitorDivRecs = records.getOrDefault(visitorDiv, mutableListOf())
                    updateRecord(homeDivRecs, glog.homeTeam, winLoseOrTie(glog.homeRuns, glog.visitorRuns))
                    updateRecord(visitorDivRecs, glog.visitorTeam, winLoseOrTie(glog.visitorRuns, glog.homeRuns))
                    records[homeDiv!!] = homeDivRecs
                    records[visitorDiv!!] = visitorDivRecs
                }
            }
            return Standings(records, date)
        }

        private fun updateRecord(teamRecords: MutableList<TeamRecord>, toUpdate: String, added: String) {
            teamRecords
                .firstOrNull { it.team == toUpdate }
                ?.also {
                    when (added) {
                        "w" -> it.w++
                        "l" -> it.l++
                        "t" -> it.t++
                    }
                }
                ?: when (added) {
                    "w" -> teamRecords.add(TeamRecord(toUpdate, w = 1, l = 0, t = 0))
                    "l" -> teamRecords.add(TeamRecord(toUpdate, w = 0, l = 1, t = 0))
                    "t" -> teamRecords.add(TeamRecord(toUpdate, w = 0, l = 0, t = 1))
                    else -> { throw UnsupportedOperationException("must add 'w' 'l' or 't'") }
                }
        }

        private fun winLoseOrTie(myScore: String, oppScore: String): String {
            return when {
                myScore.toInt() > oppScore.toInt() -> "w"
                myScore.toInt() < oppScore.toInt() -> "l"
                else -> "t"
            }
        }
    }
}

object MLBTeams {
    fun of(year: Int): MLB {
        return when {
            year < 1900 -> MLB1901
            year == 1901 -> MLB1901
            year == 1902 -> MLB1902
            year == 1903 -> MLB1903
            year in 1904..1952 -> MLB1903
            year == 1953 -> MLB1953
            year == 1954 -> MLB1954
            year in 1955..1957 -> MLB1955
            year in 1958..1960 -> MLB1958
            year == 1961 -> MLB1961
            year in 1962..1964 -> MLB1962
            year == 1965 -> MLB1965
            year in 1966..1967 -> MLB1966
            year == 1968 -> MLB1968
            year == 1969 -> MLB1969
            year in 1970..1971 -> MLB1970
            year in 1972..1976 -> MLB1972
            year in 1977..1992 -> MLB1977
            year == 1993 -> MLB1993
            year in 1994..1996 -> MLB1994
            year == 1997 -> MLB1997
            year in 1998..2004 -> MLB1998
            year in 2005..2011 -> MLB2005
            year == 2012 -> MLB2012
            year >= 2013 -> MLB2013
            else -> { throw UnsupportedOperationException("unknown year") }
        }
    }
}

fun divToName(obj: Division): String {
    return when {
        obj.javaClass.kotlin.simpleName?.startsWith("ALE")!! -> "ALE"
        obj.javaClass.kotlin.simpleName?.startsWith("ALC")!! -> "ALC"
        obj.javaClass.kotlin.simpleName?.startsWith("ALW")!! -> "ALW"
        obj.javaClass.kotlin.simpleName?.startsWith("AL")!! -> "AL"
        obj.javaClass.kotlin.simpleName?.startsWith("NLE")!! -> "NLE"
        obj.javaClass.kotlin.simpleName?.startsWith("NLC")!! -> "NLC"
        obj.javaClass.kotlin.simpleName?.startsWith("NLW")!! -> "NLW"
        obj.javaClass.kotlin.simpleName?.startsWith("NL")!! -> "NL"
        else -> ""
    }
}

/**
 * Track modern (1900+) MLB divisional structures
 */
sealed interface MLB {
    fun getDivision(team: String): Division? {
        return leagues()
            .flatMap { lg -> lg.divisions() }
            .firstOrNull { dv -> dv.teams().contains(team) }
    }
    fun leagues(): List<League>
}

sealed interface League {
    fun divisions(): List<Division>
}
sealed interface Division {
    fun teams(): List<String> // return a map of three-letter abbrs from team-abbreviations.csv to team name

    fun toTeamNames(): List<String> = teams().sorted().map {
        val t = TeamInfo.teamInfoMap[it]!!
        "${t.abbrev} ${t.city} ${t.name}"
    }
}

sealed interface NL : League
sealed interface AL : League

/* League === Division from 1900 to 1968 */
object NL1901 : Division, NL {
    override fun teams(): List<String> =
        listOf(
            "BSN", // boston braves
            "BRO", // brooklyn (boooo)
            "CHN", // cubs
            "CIN", // reds
            "NY1", // New York Giants (yayyy)
            "PHI", // phillies
            "PIT", // pirates
            "SLN", // cardinals
        )

    override fun divisions() = listOf(NL1901)
}

object AL1901 : Division, AL {
    override fun teams() = listOf(
        "BLA", // baltimore, later yankees in '03
        "PHA", // philadelphia athletics
        "BOS", // bosox
        "CHA", // chisox
        "CLE", // cleveland blues, indians, guardians
        "DET", // tigers
        "MLA", // milwaukee brewers (browns, then bal orioles)
        "WS1", // senators (twins in '61)
    )
    override fun divisions() = listOf(AL1901)
}

object MLB1901 : MLB {
    override fun leagues(): List<League> = listOf(NL1901, AL1901)
}

object AL1902 : Division, AL {
    override fun teams() = listOf("BLA", "BOS", "CHA", "CLE", "DET", "PHA", "SLA", "WS1")
    override fun divisions() = listOf(AL1902)
}

object MLB1902 : MLB {
    override fun leagues(): List<League> = listOf(NL1901, AL1902)
}

object AL1903 : Division, AL {
    override fun teams() = listOf("BOS", "CHA", "CLE", "DET", "NYA", "PHA", "SLA", "WS1")
    override fun divisions() = listOf(AL1903)
}

object MLB1903 : MLB {
    override fun leagues(): List<League> = listOf(NL1901, AL1903)
}

object NL1953 : Division, NL {
    override fun teams() = listOf("BRO", "CHN", "CIN", "MLN", "NY1", "PHI", "PIT", "SLN")
    override fun divisions() = listOf(NL1953)
}

object MLB1953 : MLB {
    override fun leagues(): List<League> = listOf(NL1953, AL1903)
}

object AL1954 : Division, AL {
    override fun teams() = listOf("BAL", "BOS", "CHA", "CLE", "DET", "NYA", "PHA", "WS1")
    override fun divisions() = listOf(AL1954)
}

object MLB1954 : MLB {
    override fun leagues(): List<League> = listOf(NL1953, AL1954)
}

object AL1955 : Division, AL {
    override fun teams() = listOf("BAL", "BOS", "CHA", "CLE", "DET", "KC1", "NYA", "WS1")
    override fun divisions() = listOf(AL1955)
}

object MLB1955 : MLB {
    override fun leagues(): List<League> = listOf(NL1953, AL1955)
}

object NL1958 : Division, NL {
    override fun teams() = listOf("CHN", "CIN", "LAN", "MLN", "PHI", "PIT", "SFN", "SLN")
    override fun divisions() = listOf(NL1958)
}

object MLB1958 : MLB {
    override fun leagues(): List<League> = listOf(NL1958, AL1955)
}

object AL1961 : Division, AL {
    override fun teams() = listOf("BAL", "BOS", "CHA", "CLE", "DET", "KC1", "LAA", "MIN", "NYA", "WS2")
    override fun divisions() = listOf(AL1961)
}

object MLB1961 : MLB {
    override fun leagues(): List<League> = listOf(NL1958, AL1961)
}

object NL1962 : Division, NL {
    override fun teams() = listOf("CHN", "CIN", "HOU", "LAN", "MLN", "NYN", "PHI", "PIT", "SFN", "SLN")
    override fun divisions() = listOf(NL1962)
}

object MLB1962 : MLB {
    override fun leagues(): List<League> = listOf(NL1962, AL1961)
}

object AL1965 : Division, AL {
    override fun teams() = listOf("BAL", "BOS", "CAL", "CHA", "CLE", "DET", "KC1", "MIN", "NYA", "WS2")
    override fun divisions(): List<Division> = listOf(AL1965)
}

object MLB1965 : MLB {
    override fun leagues(): List<League> = listOf(NL1962, AL1965)
}

object NL1966 : Division, NL {
    override fun teams() = listOf("ATL", "CHN", "CIN", "HOU", "LAN", "NYN", "PHI", "PIT", "SFN", "SLN")
    override fun divisions() = listOf(NL1966)
}

object MLB1966 : MLB {
    override fun leagues(): List<League> = listOf(NL1966, AL1965)
}

object AL1968 : Division, AL {
    override fun teams() = listOf("BAL", "BOS", "CAL", "CHA", "CLE", "DET", "MIN", "NYA", "OAK", "WS2")
    override fun divisions(): List<Division> = listOf(AL1968)
}

object MLB1968 : MLB {
    override fun leagues(): List<League> = listOf(NL1966, AL1968)
}

object ALE1969 : Division {
    override fun teams() = listOf("BAL", "BOS", "CLE", "DET", "NYA", "WS2")
}

object ALW1969 : Division {
    override fun teams() = listOf("CAL", "OAK", "CHA", "KCA", "MIN", "SE1")
}

object NLE1969 : Division {
    override fun teams() = listOf("CHN", "MON", "NYN", "PIT", "PHI", "SLN")
}

object NLW1969 : Division {
    override fun teams() = listOf("ATL", "CIN", "HOU", "LAN", "SFN", "SDN")
}

object AL1969 : AL {
    override fun divisions() = listOf(ALE1969, ALW1969)
}

object NL1969 : AL {
    override fun divisions() = listOf(NLE1969, NLW1969)
}

object MLB1969 : MLB {
    override fun leagues() = listOf(AL1969, NL1969)
}

object ALW1970 : Division {
    override fun teams() = listOf("CAL", "OAK", "CHA", "KCA", "MIN", "MIL")
}

object AL1970 : AL {
    override fun divisions() = listOf(ALE1969, ALW1970)
}

object MLB1970 : MLB {
    override fun leagues() = listOf(AL1970, NL1969)
}

object ALE1972 : Division {
    override fun teams() = listOf("BAL", "BOS", "CLE", "DET", "MIL", "NYA")
}

object ALW1972 : Division {
    override fun teams() = listOf("CAL", "OAK", "CHA", "KCA", "MIN", "TEX")
}

object AL1972 : AL {
    override fun divisions() = listOf(ALE1972, ALW1972)
}

object MLB1972 : MLB {
    override fun leagues() = listOf(AL1972, NL1969)
}

object ALE1977 : Division {
    override fun teams() = listOf("BAL", "BOS", "CLE", "DET", "MIL", "NYA", "TOR")
}

object ALW1977 : Division {
    override fun teams() = listOf("CAL", "OAK", "CHA", "KCA", "MIN", "TEX", "SEA")
}

object AL1977 : AL {
    override fun divisions() = listOf(ALE1977, ALW1977)
}

object MLB1977 : MLB {
    override fun leagues() = listOf(AL1977, NL1969)
}

object NLE1993 : Division {
    override fun teams() = listOf("CHN", "FLO", "MON", "NYN", "PIT", "PHI", "SLN")
}

object NLW1993 : Division {
    override fun teams() = listOf("ATL", "CIN", "COL", "HOU", "LAN", "SFN", "SDN")
}

object NL1993 : NL {
    override fun divisions() = listOf(NLE1993, NLW1993)
}

object MLB1993 : MLB {
    override fun leagues() = listOf(AL1977, NL1993)
}

object ALE1994 : Division {
    override fun teams() = listOf("BAL", "BOS", "DET", "NYA", "TOR")
}

object ALC1994 : Division {
    override fun teams() = listOf("CHA", "CLE", "KCA", "MIN", "MIL")
}

object ALW1994 : Division {
    override fun teams() = listOf("CAL", "OAK", "TEX", "SEA")
}

object NLE1994 : Division {
    override fun teams() = listOf("ATL", "FLO", "MON", "NYN", "PHI")
}

object NLC1994 : Division {
    override fun teams() = listOf("CHN", "CIN", "HOU", "PIT", "SLN")
}

object NLW1994 : Division {
    override fun teams() = listOf("COL", "LAN", "SDN", "SFN")
}

object AL1994 : AL {
    override fun divisions() = listOf(ALE1994, ALC1994, ALW1994)
}

object NL1994 : NL {
    override fun divisions() = listOf(NLE1994, NLC1994, NLW1994)
}

object MLB1994 : MLB {
    override fun leagues() = listOf(AL1994, NL1994)
}

object ALW1997 : Division {
    override fun teams() = listOf("ANA", "OAK", "TEX", "SEA")
}

object AL1997 : AL {
    override fun divisions() = listOf(ALE1994, ALC1994, ALW1997)
}

object MLB1997 : MLB {
    override fun leagues() = listOf(AL1997, NL1994)
}

object ALE1998 : Division {
    override fun teams() = listOf("BAL", "BOS", "NYA", "TBA", "TOR")
}

object ALC1998 : Division {
    override fun teams() = listOf("CHA", "CLE", "DET", "KCA", "MIN")
}

object NLC1998 : Division {
    override fun teams() = listOf("CHN", "CIN", "HOU", "MIL", "PIT", "SLN")
}

object NLW1998 : Division {
    override fun teams() = listOf("ARI", "COL", "LAN", "SDN", "SFN")
}

object AL1998 : AL {
    override fun divisions() = listOf(ALE1998, ALC1998, ALW1997)
}

object NL1998 : NL {
    override fun divisions() = listOf(NLE1994, NLC1998, NLW1998)
}

object MLB1998 : MLB {
    override fun leagues() = listOf(AL1998, NL1998)
}

object NLE2005 : Division {
    override fun teams() = listOf("ATL", "FLO", "NYN", "PHI", "WAS")
}

object NL2005 : NL {
    override fun divisions() = listOf(NLE2005, NLC1998, NLW1998)
}

object MLB2005 : MLB {
    override fun leagues() = listOf(AL1998, NL2005)
}

object NLE2012 : Division {
    override fun teams() = listOf("ATL", "MIA", "NYN", "PHI", "WAS")
}

object NL2012 : NL {
    override fun divisions() = listOf(NLE2012, NLC1998, NLW1998)
}

object MLB2012 : MLB {
    override fun leagues() = listOf(AL1998, NL2012)
}

object ALW2013 : Division {
    override fun teams() = listOf("ANA", "HOU", "OAK", "TEX", "SEA")
}

object NLC2013 : Division {
    override fun teams() = listOf("CHN", "CIN", "MIL", "PIT", "SLN")
}

object AL2013 : AL {
    override fun divisions() = listOf(ALE1998, ALC1998, ALW2013)
}

object NL2013 : NL {
    override fun divisions() = listOf(NLE2012, NLC2013, NLW1998)
}

object MLB2013 : MLB {
    override fun leagues() = listOf(AL2013, NL2013)
}
