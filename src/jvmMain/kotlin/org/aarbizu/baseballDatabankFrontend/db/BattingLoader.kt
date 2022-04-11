package org.aarbizu.baseballDatabankFrontend.db

import java.io.File

class BattingLoader : CsvLoader {
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
                            G int DEFAULT null,
                            AB int DEFAULT null,
                            R int DEFAULT null,
                            H int DEFAULT null,
                            "2B" int DEFAULT null,
                            "3B" int DEFAULT null,
                            HR int DEFAULT null,
                            RBI int DEFAULT null,
                            SB int DEFAULT null,
                            CS int DEFAULT null,
                            BB int DEFAULT null,
                            SO int DEFAULT null,
                            IBB varchar,
                            HBP varchar,
                            SH varchar,
                            SF varchar,
                            GIDP varchar
                        ) AS
                        SELECT * FROM CSVREAD('${csvFile.absolutePath}');
                    """.trimIndent()
                )
            }
        }
    }
}
