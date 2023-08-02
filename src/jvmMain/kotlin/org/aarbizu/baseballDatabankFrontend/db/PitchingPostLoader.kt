package org.aarbizu.baseballDatabankFrontend.db

class PitchingPostLoader : CsvLoader {
    override fun load(db: DBProvider, csvFile: DataLoader.CsvFile) {
        val tableName = csvFile.nameWithoutExtension
        db.getConnection().use {
            it.createStatement().use { stmt ->
                stmt.execute(
                    """
                        DROP TABLE IF EXISTS $tableName;
                        CREATE TABLE $tableName(
                            playerID varchar,
                            yearID int DEFAULT null,
                            round varchar,
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
                            BAOpp double precision DEFAULT null,
                            ERA double precision DEFAULT null,
                            IBB int DEFAULT null,
                            WP int DEFAULT null,
                            HBP int DEFAULT null,
                            BK int DEFAULT null,
                            BFP int DEFAULT null,
                            GF int DEFAULT null,
                            R int DEFAULT null,
                            SH int DEFAULT null,
                            SF int DEFAULT null,
                            GIDP int DEFAULT null
                        ) AS
                        SELECT 
                            playerID,
                            yearId,
                            round,
                            teamID,
                            lgID,
                            W,
                            L,
                            G,
                            GS,
                            CG,
                            SHO,
                            SV,
                            IPouts,
                            H,
                            ER,
                            HR,
                            BB,
                            SO,
                            BAOpp,
                            CASE WHEN ERA = 'inf' THEN 'Infinity' ELSE ERA END,
                            IBB,
                            WP,
                            HBP,
                            BK,
                            BFP,
                            GF,
                            R,
                            SH,
                            SF,
                            GIDP
                        FROM CSVREAD('${csvFile.absolutePath}');
                    """
                        .trimIndent(),
                )
            }
        }
    }
}
