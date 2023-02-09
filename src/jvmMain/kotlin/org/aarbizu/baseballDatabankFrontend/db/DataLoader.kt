package org.aarbizu.baseballDatabankFrontend.db

import org.slf4j.LoggerFactory
import java.io.File

class DataLoader(private val db: DBProvider, private val csvHome: String) {
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
            "PitchingPost" to PitchingPostLoadder(),
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

    fun getCsvFiles(): List<File> {
        return File(csvHome).walkTopDown().filter { it.isFile && it.extension == "csv" }.toList()
    }
}
