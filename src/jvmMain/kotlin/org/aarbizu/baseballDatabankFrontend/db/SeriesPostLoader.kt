package org.aarbizu.baseballDatabankFrontend.db

import java.io.File

class SeriesPostLoader : CsvLoader {
    override fun load(db: DBProvider, csvFile: File) {
        val tableName = csvFile.nameWithoutExtension
        db.getConnection().use {
            it.createStatement().use { stmt ->
                stmt.execute(
                    """
                        DROP TABLE IF EXISTS $tableName;
                        CREATE TABLE $tableName(
                            yearID int DEFAULT null,
                            round text,
                            teamIDwinner text,
                            lgIDwinner text,
                            teamIDloser text,
                            lgIDloser text,
                            wins int DEFAULT null,
                            losses int DEFAULT null,
                            ties int DEFAULT null
                        ) AS
                        SELECT * FROM CSVREAD('${csvFile.absolutePath}');
                    """
                        .trimIndent(),
                )
            }
        }
    }
}
