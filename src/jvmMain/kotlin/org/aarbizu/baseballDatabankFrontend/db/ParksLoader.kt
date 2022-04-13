package org.aarbizu.baseballDatabankFrontend.db

import java.io.File

class ParksLoader : CsvLoader {
    override fun load(db: DBProvider, csvFile: File) {
        val tableName = csvFile.nameWithoutExtension
        db.getConnection().use {
            it.createStatement().use { stmt ->
                stmt.execute(
                    """
                        DROP TABLE IF EXISTS $tableName;
                        CREATE TABLE $tableName(
                            "park.key" varchar,
                            "park.name" varchar,
                            "park.alias" varchar,
                            city varchar,
                            state varchar,
                            country varchar
                        ) AS
                        SELECT * FROM CSVREAD('${csvFile.absolutePath}');
                    """.trimIndent()
                )
            }
        }
    }
}
