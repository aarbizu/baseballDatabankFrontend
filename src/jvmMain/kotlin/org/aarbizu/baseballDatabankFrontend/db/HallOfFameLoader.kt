package org.aarbizu.baseballDatabankFrontend.db

import java.io.File

class HallOfFameLoader : CsvLoader {
    override fun load(db: DBProvider, csvFile: File) {
        val tableName = csvFile.nameWithoutExtension
        db.getConnection().use {
            it.createStatement().use { stmt ->
                stmt.execute(
                    """
                        DROP TABLE IF EXISTS $tableName;
                        CREATE TABLE $tableName(
                            playerID varchar,
                            yearid int DEFAULT null,
                            votedBy varchar,
                            ballots int DEFAULT null,
                            needed int DEFAULT null,
                            votes int DEFAULT null,
                            inducted varchar,
                            category varchar,
                            needed_note varchar
                        ) AS
                        SELECT * FROM CSVREAD('${csvFile.absolutePath}');
                    """
                        .trimIndent(),
                )
            }
        }
    }
}
