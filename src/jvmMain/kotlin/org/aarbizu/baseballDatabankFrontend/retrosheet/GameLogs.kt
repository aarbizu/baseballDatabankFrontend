package org.aarbizu.baseballDatabankFrontend.retrosheet

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.util.zip.ZipInputStream

/**
 * Retrieve GameLog info from retrosheet archive.
 */
val RETROSHEET_GAMELOGS_ARCHIVE = "${File.separator}retrosheet${File.separator}gl1871_2022.zip"
class GameLogs(private val logProvider: () -> InputStream? = gameLogArchiveProvider) {

    private val seasonGameLogCache: LoadingCache<String, List<SimpleGameLog>> = CacheBuilder.newBuilder()
        .build(
            CacheLoader.from { year: String? -> year?.let { getGameLogs(year, logProvider) } }
        )

    fun getGameLogs(year: String): List<SimpleGameLog> {
        return seasonGameLogCache.get(year)
    }

    private fun getGameLogs(year: String, provider: () -> InputStream?): List<SimpleGameLog> {
        return getGameLogFromArchive(year, provider)
            .filter {
                it.isNotEmpty()
            }
            .map { it.replace("\"", "") }
            .map {
                it.split(",")
            }.map {
                val visitingTeam = TeamInfo.teamInfoMap[it[3]]
                val homeTeam = TeamInfo.teamInfoMap[it[6]]
                SimpleGameLog(
                    it[0],
                    it[3],
                    "${visitingTeam?.league}",
                    it[9],
                    it[3],
                    it[6],
                    "${homeTeam?.league}",
                    it[10],
                    it[6]
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
        val gameLogArchiveProvider: () -> InputStream? = {
            object { }.javaClass.getResourceAsStream(RETROSHEET_GAMELOGS_ARCHIVE)
        }
    }
}