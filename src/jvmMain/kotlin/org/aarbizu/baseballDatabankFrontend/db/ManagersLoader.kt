package org.aarbizu.baseballDatabankFrontend.db

import java.io.File

class ManagersLoader : CsvLoader {
    override fun load(db: DBProvider, csvFile: File) {
        val tableName = csvFile.nameWithoutExtension
        db.getConnection().use {
            it.createStatement().use { stmt ->
                stmt.execute(
                    """
                        DROP TABLE IF EXISTS $tableName;
                        CREATE TABLE $tableName(
                            playerID varchar,
                            yearID int DEFAULT null,
                            teamID varchar,
                            lgID varchar,
                            inseason int DEFAULT null,
                            G int DEFAULT null,
                            W int DEFAULT null,
                            L int DEFAULT null,
                            rank int DEFAULT null,
                            plyrMgr varchar
                        ) AS
                        SELECT * FROM CSVREAD('${csvFile.absolutePath}');
                    """.trimIndent()
                )
            }
        }    }

}
