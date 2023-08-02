package org.aarbizu.baseballDatabankFrontend.db

import org.h2.util.IOUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.extension
import kotlin.io.path.name

val CSV_FILES_PATH = "${File.separator}csv"
val defaultCsvLocation: () -> URI? = { object { }.javaClass.getResource(CSV_FILES_PATH)?.toURI() }

class DataLoader(
    private val db: DBProvider,
    private val csvLocationProvider: () -> URI? = defaultCsvLocation,
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val fileLoaders =
        mapOf(
            "AllstarFull" to AllStarFullLoader(),
            "Appearances" to AppearancesLoader(),
            "Batting" to BattingLoader(),
            "BattingPost" to BattingPostLoader(),
            "Fielding" to FieldingLoader(),
            "FieldingOF" to FieldingOFLoader(),
            "FieldingOFsplit" to FieldingOFsplitLoader(),
            "FieldingPost" to FieldingPostLoader(),
            "HomeGames" to HomeGamesLoader(),
            "Managers" to ManagersLoader(),
            "ManagersHalf" to ManagersHalfLoader(),
            "Parks" to ParksLoader(),
            "People" to PeopleLoader(),
            "Pitching" to PitchingLoader(),
            "PitchingPost" to PitchingPostLoader(),
            "SeriesPost" to SeriesPostLoader(),
            "Teams" to TeamsLoader(),
            "TeamsFranchises" to TeamsFranchisesLoader(),
            "TeamsHalf" to TeamsHalfLoader(),
            "AwardsManagers" to AwardsLoader(),
            "AwardsPlayers" to AwardsLoader(),
            "AwardsShareManagers" to AwardsShareLoader(),
            "AwardsSharePlayers" to AwardsShareLoader(),
            "CollegePlaying" to CollegePlayingLoader(),
            "HallOfFame" to HallOfFameLoader(),
            "Salaries" to SalariesLoader(),
            "Schools" to SchoolsLoader(),
            "Teams" to TeamsLoader(),
        )

    fun loadAllFiles() {
        var count = 0
        getCsvFiles().forEach {
            log.info("loading $it")
            fileLoaders[it.nameWithoutExtension]?.load(db, it).also { count += 1 }
            it.csvData.delete()
        }
        log.info("loaded $count files")
    }

    fun buildIndexes() {
        log.info("building indexes")
        db.getConnection().use {
            log.info("building people-player index")
            it.createStatement().use { stmt ->
                stmt.execute(
                    """
                        DROP INDEX IF EXISTS PEOPLE_PLAYER;
                        CREATE INDEX PEOPLE_PLAYER ON people ( playerID );
                    """
                        .trimIndent(),
                )
            }

            log.info("building appearances-team index")
            it.createStatement().use { stmt ->
                stmt.execute(
                    """
                        DROP INDEX IF EXISTS APP_TEAM;
                        CREATE INDEX APP_TEAM ON appearances ( teamID );
                    """
                        .trimIndent(),
                )
            }

            log.info("buildding appearances-player index")
            it.createStatement().use { stmt ->
                stmt.execute(
                    """
                        DROP INDEX IF EXISTS APP_PLAYER;
                        CREATE INDEX APP_PLAYER ON appearances ( playerID );
                    """
                        .trimIndent(),
                )
            }

            log.info("building manager-player index")
            it.createStatement().use { stmt ->
                stmt.execute(
                    """
                        DROP INDEX IF EXISTS MGR_PLAYER;
                        CREATE INDEX MGR_PLAYER ON managers ( playerID, plyrmgr );
                    """
                        .trimIndent(),
                )
            }
        }
    }

    fun getCsvFiles(): List<CsvFile> {
        return getCsvFiles(csvLocationProvider.invoke())
    }

    private fun getCsvFiles(dir: URI?): List<CsvFile> {
        val csvFiles: MutableList<CsvFile> = mutableListOf()
        val path = dir?.let {
            if (dir.scheme == "jar") {
                val fs = FileSystems.newFileSystem(dir, emptyMap<String, Any>())
                fs.getPath(CSV_FILES_PATH)
            } else {
                Paths.get(dir)
            }
        }
        path?.let {
            Files.walkFileTree(path, GetFileVisitor(csvFiles))
        }
        return csvFiles
    }

    class GetFileVisitor(private val files: MutableList<CsvFile>) : SimpleFileVisitor<Path>() {
        override fun visitFile(file: Path?, attrs: BasicFileAttributes?): FileVisitResult {
            file?.let {
                if (it.extension == "csv") {
                    val f = Files.newInputStream(it)
                    val name = it.name.substringAfter('_').substringBefore('.')
                    val tempFile = File.createTempFile(name, it.extension)
                    IOUtils.copy(f, FileOutputStream(tempFile))
                    files.add(CsvFile(name, it.toAbsolutePath().toString(), tempFile))
                }
            }
            return FileVisitResult.CONTINUE
        }
    }

    data class CsvFile(val nameWithoutExtension: String, val originalPath: String, val csvData: File) {
        val absolutePath: String = csvData.absolutePath
    }
}
