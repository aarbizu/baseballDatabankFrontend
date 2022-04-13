package org.aarbizu.baseballDatabankFrontend.db

import java.io.File

class TeamsFranchisesLoader : CsvLoader {
    override fun load(db: DBProvider, csvFile: File) {
        val tableName = csvFile.nameWithoutExtension
        db.getConnection().use {
            it.createStatement().use { stmt ->
                stmt.execute(
                    """
                        DROP TABLE IF EXISTS $tableName;
                        CREATE TABLE $tableName(
                            franchID varchar,
                            franchName varchar,
                            active varchar,
                            NAassoc varchar
                         ) AS
                        SELECT * FROM CSVREAD('${csvFile.absolutePath}');
                    """.trimIndent()
                )
            }
        }
    }
}
