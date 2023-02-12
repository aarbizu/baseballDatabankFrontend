package org.aarbizu.baseballDatabankFrontend.db

import java.io.File

class AllStarFullLoader : CsvLoader {
    override fun load(db: DBProvider, csvFile: File) {
        val tableName = csvFile.nameWithoutExtension
        db.getConnection().use {
            it.createStatement().use { stmt ->
                stmt.execute(
                    """
                        DROP TABLE IF EXISTS $tableName;
                        CREATE TABLE $tableName(
                            playerId varchar,
                            yearId int default null,
                            gameNum int DEFAULT null,
                            gameID varchar,
                            teamID varchar,
                            lgID varchar,
                            GP int DEFAULT null,
                            startingPos int DEFAULT null
                        ) AS 
                        SELECT * FROM CSVREAD('${csvFile.absolutePath}');
                    """
                        .trimIndent(),
                )
            }
        }
    }
}
