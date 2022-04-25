package org.aarbizu.baseballDatabankFrontend.db

import java.io.File

class FieldingLoader : CsvLoader {
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
                            stint int DEFAULT null,
                            teamID varchar,
                            lgID varchar,
                            POS varchar,
                            G int DEFAULT null,
                            GS varchar,
                            InnOuts int DEFAULT null,
                            PO int DEFAULT null,
                            A int DEFAULT null,
                            E int DEFAULT null,
                            DP int DEFAULT null,
                            PB varchar,
                            WP varchar,
                            SB varchar,
                            CS varchar,
                            ZR varchar
                        ) AS
                        SELECT * FROM CSVREAD('${csvFile.absolutePath}');
                    """.trimIndent()
                )
            }
        }
    }
}
