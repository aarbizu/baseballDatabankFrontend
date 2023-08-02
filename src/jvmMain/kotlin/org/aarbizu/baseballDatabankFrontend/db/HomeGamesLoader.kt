package org.aarbizu.baseballDatabankFrontend.db

class HomeGamesLoader : CsvLoader {
    override fun load(db: DBProvider, csvFile: DataLoader.CsvFile) {
        val tableName = csvFile.nameWithoutExtension
        db.getConnection().use {
            it.createStatement().use { stmt ->
                stmt.execute(
                    """
                        DROP TABLE IF EXISTS $tableName;
                        CREATE TABLE $tableName(
                            "year.key" int DEFAULT null,
                            "league.key" varchar,
                            "team.key" varchar,
                            "park.key" varchar,
                            "span.first" varchar,
                            "span.last" varchar,
                            games int DEFAULT null,
                            openings int DEFAULT null,
                            attendance int DEFAULT null
                        ) AS
                        SELECT * FROM CSVREAD('${csvFile.absolutePath}');
                    """
                        .trimIndent(),
                )
            }
        }
    }
}
