package org.aarbizu.baseballDatabankFrontend.db

class HallOfFameLoader : CsvLoader {
    override fun load(db: DBProvider, csvFile: DataLoader.CsvFile) {
        val tableName = csvFile.nameWithoutExtension
        db.getConnection().use {
            it.createStatement().use { stmt ->
                stmt.execute(
                    """
                        DROP TABLE IF EXISTS $tableName;
                        CREATE TABLE $tableName(
                            playerID varchar,
                            yearid int DEFAULT null,
                            votedBy varchar,
                            ballots int DEFAULT null,
                            needed int DEFAULT null,
                            votes int DEFAULT null,
                            inducted varchar,
                            category varchar,
                            needed_note varchar
                        ) AS
                        SELECT
                            playerID,
                            yearid,
                            votedBy,
                            CASE WHEN ballots = 'NA' THEN -1 ELSE ballots END,
                            CASE WHEN needed = 'NA' THEN -1 ELSE ballots END,
                            CASE WHEN votes = 'NA' THEN -1 ELSE ballots END,
                            inducted,
                            category,
                            needed_note
                        FROM CSVREAD('${csvFile.absolutePath}');
                    """
                        .trimIndent(),
                )
            }
        }
    }
}
