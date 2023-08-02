package org.aarbizu.baseballDatabankFrontend.db

class AwardsLoader : CsvLoader {
    override fun load(db: DBProvider, csvFile: DataLoader.CsvFile) {
        val tableName = csvFile.nameWithoutExtension
        db.getConnection().use {
            it.createStatement().use { stmt ->
                stmt.execute(
                    """
                        DROP TABLE IF EXISTS $tableName;
                        CREATE TABLE $tableName(
                            playerID varchar,
                            awardID varchar,
                            yearID int DEFAULT null,
                            lgID varchar,
                            tie varchar,
                            notes varchar
                        ) AS 
                        SELECT * FROM CSVREAD('${csvFile.absolutePath}');
                    """
                        .trimIndent(),
                )
            }
        }
    }
}
