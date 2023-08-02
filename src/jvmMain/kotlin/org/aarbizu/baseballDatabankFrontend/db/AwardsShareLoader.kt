package org.aarbizu.baseballDatabankFrontend.db

class AwardsShareLoader : CsvLoader {
    override fun load(db: DBProvider, csvFile: DataLoader.CsvFile) {
        val tableName = csvFile.nameWithoutExtension
        db.getConnection().use {
            it.createStatement().use { stmt ->
                stmt.execute(
                    """
                        DROP TABLE IF EXISTS $tableName;
                        CREATE TABLE $tableName(
                            awardID varchar,
                            yearID int DEFAULT null,
                            lgID varchar,
                            playerID varchar,
                            pointsWon double precision DEFAULT null,
                            pointsMax double precision DEFAULT null,
                            votesFirst double precision DEFAULT null
                        ) AS
                        SELECT * FROM CSVREAD('${csvFile.absolutePath}');
                    """
                        .trimIndent(),
                )
            }
        }
    }
}
