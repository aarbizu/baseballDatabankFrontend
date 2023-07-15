package org.aarbizu.baseballDatabankFrontend.retrosheet

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.zip.ZipInputStream

/**
 * Retrieve GameLog info from retrosheet archive.
 */
class GameLogs {

    //TODO cache the read from the archive so we're not doing a full processing every time

    fun getGameLogs(year: String): List<SimpleGameLog> {
        return getGameLogs(year, gameLogArchiveProvider)
    }

    internal fun getGameLogs(year: String, provider: () -> InputStream?): List<SimpleGameLog> {
        return getGameLogFromArchive(year, provider)
            .filter {
                it.isNotEmpty()
            }
            .map {
                it.split(",")
            }.map {
                val visitingTeam = TeamInfo.teamInfoMap[it[3].replace("\"", "")]
                val homeTeam = TeamInfo.teamInfoMap[it[6].replace("\"", "")]
                SimpleGameLog(
                    it[0].replace("\"", ""),
                    "${visitingTeam?.city} ${visitingTeam?.name}",
                    "${visitingTeam?.league}",
                    it[9].replace("\"", ""),
                    "${homeTeam?.city} ${homeTeam?.name}",
                    "${homeTeam?.league}",
                    it[10].replace("\"", "")
                )
            }
    }

    internal fun getGameLogFromArchive(year: String, archiveProvider: () -> InputStream?): List<String> {
        archiveProvider.invoke()?.let {
            val zis = ZipInputStream(it)
            var entry = zis.nextEntry
            while (entry?.name != "gl$year.txt") {
                entry = zis.nextEntry
            }
            check(entry.size < Int.MAX_VALUE)
            val entryOut = ByteArrayOutputStream(entry.size.toInt())
            zis.copyTo(entryOut)
            zis.close()
            return entryOut.toByteArray().toString(Charsets.UTF_8).split(System.lineSeparator())
        }
        return emptyList()
    }

    companion object {
        val gameLogArchiveProvider: () -> InputStream? = { object { }.javaClass.getResourceAsStream("gl1871_2022.zip") }
    }
}