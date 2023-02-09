package org.aarbizu.baseballDatabankFrontend.db

import java.io.File

class AwardsShareLoader : CsvLoader {
    override fun load(db: DBProvider, csvFile: File) {
        val tableName = csvFile.nameWithoutExtension
        db.getConnection().use {
            it.createStatement().use { stmt ->
                stmt.execute(
                    """
                        DROP TABLE IF EXISTS $tableName;
                        CREATE TABLE $tableName(
                            awardID varchar,
                            yearID int DEFAULT null,
                            lgID varchar,
                            playerID varchar,
                            pointsWon double precision DEFAULT null,
                            pointsMax double precision DEFAULT null,
                            votesFirst double precision DEFAULT null
                        ) AS
                        SELECT * FROM CSVREAD('${csvFile.absolutePath}');
                    """
                        .trimIndent(),
                )
            }
        }
    }
}
