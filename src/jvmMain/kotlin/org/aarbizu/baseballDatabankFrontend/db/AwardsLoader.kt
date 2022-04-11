package org.aarbizu.baseballDatabankFrontend.db

import java.io.File

class AwardsLoader : CsvLoader {
    override fun load(db: DBProvider, csvFile: File) {
        val tableName = csvFile.nameWithoutExtension
        db.getConnection().use {
            it.createStatement().use { stmt ->
                stmt.execute(
                    """
                        DROP TABLE IF EXISTS $tableName;
                        CREATE TABLE $tableName(
                            playerID varchar,
                            awardID varchar,
                            yearID int DEFAULT null,
                            lgID varchar,
                            tie varchar,
                            notes varchar
                        ) AS 
                        SELECT * FROM CSVREAD('${csvFile.absolutePath}');
                    """.trimIndent()
                )
            }
        }
    }
}
