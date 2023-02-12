package org.aarbizu.baseballDatabankFrontend.db

import java.io.File

class TeamsLoader : CsvLoader {
    override fun load(db: DBProvider, csvFile: File) {
        // upstream/Teams.csv vs core/Teams.csv -- not sure what the differences are, but, handle
        // them here
        if (csvFile.absolutePath.contains("/core/")) {
            loadCore(db, csvFile)
        } else {
            loadUpstream(db, csvFile)
        }
    }

    private fun loadCore(db: DBProvider, csvFile: File) {
        val tableName = csvFile.nameWithoutExtension
        db.getConnection().use {
            it.createStatement().use { stmt ->
                stmt.execute(
                    """
                        DROP TABLE IF EXISTS $tableName;
                        CREATE TABLE $tableName(
                            yearID int DEFAULT null,
                            lgID varchar,
                            teamID varchar,
                            franchID varchar,
                            divID varchar,
                            Rank int DEFAULT null,
                            G int DEFAULT null,
                            Ghome varchar,
                            W int DEFAULT null,
                            L int DEFAULT null,
                            DivWin varchar,
                            WCWin varchar,
                            LgWin varchar,
                            WSWin varchar,
                            R int DEFAULT null,
                            AB int DEFAULT null,
                            H int DEFAULT null,
                            "2B" int DEFAULT null,
                            "3B" int DEFAULT null,
                            HR int DEFAULT null,
                            BB int DEFAULT null,
                            SO int DEFAULT null,
                            SB int DEFAULT null,
                            CS varchar,
                            HBP varchar,
                            SF varchar,
                            RA int DEFAULT null,
                            ER int DEFAULT null,
                            ERA double precision DEFAULT null,
                            CG int DEFAULT null,
                            SHO int DEFAULT null,
                            SV int DEFAULT null,
                            IPouts int DEFAULT null,
                            HA int DEFAULT null,
                            HRA int DEFAULT null,
                            BBA int DEFAULT null,
                            SOA int DEFAULT null,
                            E int DEFAULT null,
                            DP varchar,
                            FP double precision DEFAULT null,
                            "name" varchar,
                            park varchar,
                            attendance varchar,
                            BPF int DEFAULT null,
                            PPF int DEFAULT null,
                            teamIDBR varchar,
                            teamIDlahman45 varchar,
                            teamIDretro varchar
                        ) AS
                        SELECT * FROM CSVREAD('${csvFile.absolutePath}');
                    """
                        .trimIndent(),
                )
            }
        }
    }

    private fun loadUpstream(db: DBProvider, csvFile: File) {
        val tableName = csvFile.nameWithoutExtension
        db.getConnection().use {
            it.createStatement().use { stmt ->
                stmt.execute(
                    """
                        DROP TABLE IF EXISTS upstream_$tableName;
                        CREATE TABLE upstream_$tableName (
                            yearID varchar,
                            lgID varchar,
                            teamID varchar,
                            franchID varchar,
                            divID varchar,
                            Rank int default null,
                            Ghome varchar,
                            DivWin varchar,
                            WCWin varchar,
                            LgWin varchar,
                            WSWin varchar,
                            "name" varchar,
                            park varchar,
                            attendance varchar,
                            BPF int default null,
                            PPF int default null,
                            teamIDBR varchar,
                            teamIDlahman45 varchar,
                            teamIDretro varchar
                        ) AS
                        SELECT * FROM CSVREAD('${csvFile.absolutePath}');
                    """
                        .trimIndent(),
                )
            }
        }
    }
}
