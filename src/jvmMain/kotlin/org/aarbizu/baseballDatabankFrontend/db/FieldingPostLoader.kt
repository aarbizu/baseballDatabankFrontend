package org.aarbizu.baseballDatabankFrontend.db

class FieldingPostLoader : CsvLoader {
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
                            teamID varchar,
                            lgID varchar,
                            round varchar,
                            POS varchar,
                            G int DEFAULT null,
                            GS int DEFAULT null,
                            InnOuts int DEFAULT null,
                            PO int DEFAULT null,
                            A int DEFAULT null,
                            E int DEFAULT null,
                            DP int DEFAULT null,
                            TP int DEFAULT null,
                            PB varchar,
                            SB varchar,
                            CS varchar
                        ) AS
                        SELECT * FROM CSVREAD('${csvFile.absolutePath}');
                    """
                        .trimIndent(),
                )
            }
        }
    }
}
