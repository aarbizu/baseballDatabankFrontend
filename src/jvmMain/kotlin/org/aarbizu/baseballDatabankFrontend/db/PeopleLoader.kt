package org.aarbizu.baseballDatabankFrontend.db

import java.io.File

class PeopleLoader : CsvLoader {
    override fun load(db: DBProvider, csvFile: File) {
        val tableName = csvFile.nameWithoutExtension
        db.getConnection().use {
            it.createStatement().use { stmt ->
                stmt.execute(
                    """
                        DROP TABLE IF EXISTS $tableName;
                        CREATE TABLE $tableName(
                            playerID varchar,
                            birthYear int DEFAULT null,
                            birthMonth int DEFAULT null,
                            birthDay int DEFAULT null,
                            birthCountry varchar,
                            birthState varchar,
                            birthCity varchar,
                            deathYear varchar,
                            deathMonth varchar,
                            deathDay varchar,
                            deathCountry varchar,
                            deathState varchar,
                            deathCity varchar,
                            nameFirst varchar,
                            nameLast varchar,
                            nameGiven varchar,
                            "weight" int DEFAULT null,
                            height int DEFAULT null,
                            bats varchar,
                            throws varchar,
                            debut varchar,
                            finalGame varchar,
                            retroID varchar,
                            bbrefID varchar
                        ) AS 
                        SELECT * FROM CSVREAD('${csvFile.absolutePath}');
                    """.trimIndent()
                )
            }
        }
    }
}
