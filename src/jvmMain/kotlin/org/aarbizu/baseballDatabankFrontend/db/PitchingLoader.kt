package org.aarbizu.baseballDatabankFrontend.db

import java.io.File

class PitchingLoader : CsvLoader {
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
                            W int DEFAULT null,
                            L int DEFAULT null,
                            G int DEFAULT null,
                            GS int DEFAULT null,
                            CG int DEFAULT null,
                            SHO int DEFAULT null,
                            SV int DEFAULT null,
                            IPouts int DEFAULT null,
                            H int DEFAULT null,
                            ER int DEFAULT null,
                            HR int DEFAULT null,
                            BB int DEFAULT null,
                            SO int DEFAULT null,
                            BAOpp varchar,
                            ERA double precision DEFAULT null,
                            IBB varchar,
                            WP varchar,
                            HBP varchar,
                            BK int DEFAULT null,
                            BFP varchar,
                            GF varchar,
                            R int DEFAULT null,
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
