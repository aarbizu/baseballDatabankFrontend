package org.aarbizu.baseballDatabankFrontend.db

class AppearancesLoader : CsvLoader {
    override fun load(db: DBProvider, csvFile: DataLoader.CsvFile) {
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
                            G_all int DEFAULT null,
                            GS varchar,
                            G_batting int DEFAULT null,
                            G_defense int DEFAULT null,
                            G_p int DEFAULT null,
                            G_c int DEFAULT null,
                            G_1b int DEFAULT null,
                            G_2b int DEFAULT null,
                            G_3b int DEFAULT null,
                            G_ss int DEFAULT null,
                            G_lf int DEFAULT null,
                            G_cf int DEFAULT null,
                            G_rf int DEFAULT null,
                            G_of int DEFAULT null,
                            G_dh varchar,
                            G_ph varchar,
                            G_pr varchar
                        ) AS
                        SELECT * FROM CSVREAD('${csvFile.absolutePath}');
                    """
                        .trimIndent(),
                )
            }
        }
    }
}
