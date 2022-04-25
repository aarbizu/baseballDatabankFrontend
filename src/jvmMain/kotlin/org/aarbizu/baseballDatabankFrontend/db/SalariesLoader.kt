package org.aarbizu.baseballDatabankFrontend.db

import java.io.File

class SalariesLoader : CsvLoader {
    override fun load(db: DBProvider, csvFile: File) {
        val tableName = csvFile.nameWithoutExtension
        db.getConnection().use {
            it.createStatement().use { stmt ->
                stmt.execute(
                    """
                        DROP TABLE IF EXISTS $tableName;
                        CREATE TABLE $tableName(
                            yearID int DEFAULT null,
                            teamID varchar,
                            lgID varchar,
                            playerID varchar,
                            salary int DEFAULT null
                        ) AS
                        SELECT * FROM CSVREAD('${csvFile.absolutePath}');
                    """.trimIndent()
                )
            }
        }
    }
}
