package org.aarbizu.baseballDatabankFrontend.db

class TeamsHalfLoader : CsvLoader {
    override fun load(db: DBProvider, csvFile: DataLoader.CsvFile) {
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
                            Half int DEFAULT null,
                            divID varchar,
                            DivWin varchar,
                            "Rank" int DEFAULT null,
                            G int DEFAULT null,
                            W int DEFAULT null,
                            L int DEFAULT null
                        ) AS
                        SELECT * FROM CSVREAD('${csvFile.absolutePath}');
                    """
                        .trimIndent(),
                )
            }
        }
    }
}
