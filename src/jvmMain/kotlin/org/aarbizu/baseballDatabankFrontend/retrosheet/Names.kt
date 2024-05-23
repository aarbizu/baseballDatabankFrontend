package org.aarbizu.baseballDatabankFrontend.retrosheet

import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.util.zip.ZipInputStream

/**
 * Generic names info, pulled from SSA.gov data files
 */
val RARE_NAMES = "retrosheet${File.separator}rarenames-out.zip"
val COMMON_NAMES = "retrosheet${File.separator}commonnames-out.zip"

class Names {

    fun initialize() {
        rareNames = readHistoricalNamesData(rareNamesProvider)
        commonNames = readHistoricalNamesData(commonNamesProvider)
    }

    fun readHistoricalNamesData(historicalNamesProvider: () -> InputStream?): Set<String> {
        return historicalNamesProvider.invoke()?.let { archive ->
            ZipInputStream(archive).use { zip ->
                generateSequence { zip.nextEntry }
                    .filterNot { it.isDirectory }
                    .map {
                        ByteArrayInputStream(zip.readAllBytes()).bufferedReader()
                    }
                    .map {
                        it.readLines()
                    }
                    .flatten().toSet()
            }
        }.orEmpty()
    }

    companion object {
        val rareNamesProvider: () -> InputStream? = {
            object {}.javaClass.getResourceAsStream("${File.separator}$RARE_NAMES")
        }

        val commonNamesProvider: () -> InputStream? = {
            object {}.javaClass.getResourceAsStream("${File.separator}$COMMON_NAMES")
        }

        var rareNames: Set<String> = emptySet()
        var commonNames: Set<String> = emptySet()
    }
}
