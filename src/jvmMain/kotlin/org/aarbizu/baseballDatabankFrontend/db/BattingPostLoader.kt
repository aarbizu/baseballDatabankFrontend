package org.aarbizu.baseballDatabankFrontend.db

import java.io.File

class BattingPostLoader : CsvLoader {
    override fun load(db: DBProvider, csvFile: File) {
        val tableName = csvFile.nameWithoutExtension
        db.getConnection().use {
            it.createStatement().use { stmt ->
                stmt.execute(
                    """
                        DROP TABLE IF EXISTS $tableName;
                        CREATE TABLE $tableName(
                            yearID int DEFAULT null,
                            round varchar,
                            playerID varchar,
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
                            CS varchar,
                            BB int DEFAULT null,
                            SO int DEFAULT null,
                            IBB int DEFAULT null,
                            HBP int DEFAULT null,
                            SH int DEFAULT null,
                            SF int DEFAULT null,
                            GIDP int DEFAULT null
                        ) AS
                        SELECT * FROM CSVREAD('${csvFile.absolutePath}');
                    """
                        .trimIndent(),
                )
            }
        }
    }
}
