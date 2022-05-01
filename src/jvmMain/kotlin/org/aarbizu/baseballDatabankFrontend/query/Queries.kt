package org.aarbizu.baseballDatabankFrontend.query

fun playerNameRegexSql(
    first: Boolean = true,
    last: Boolean = true,
    caseSensitive: Boolean = false
): String {
    val nameClauseColumn =
        if (first && last) {
            "namefirst || ' ' || namelast"
        } else if (first) {
            "namefirst"
        } else {
            "namelast"
        }

    return """
        SELECT 
            COALESCE(namefirst || ' ' || namelast, 'unknown') as name,
            COALESCE(birthyear, 0) || '-' || COALESCE(birthmonth, 0) || '-' || COALESCE(birthday, 0) as born,
            COALESCE(debut, 'unknown') as debut,
            COALESCE(finalgame, 'unknown') as finalgame,
            COALESCE(playerid, 'unknown') as playerid,
            COALESCE(bbrefid, 'unknown') as bbrefid
        FROM people
        WHERE REGEXP_LIKE($nameClauseColumn, ?, '${if (caseSensitive) "c" else { "i" }}')
    """.trimIndent()
}

fun singleSeasonHrTotalsSql(firstOnly: Boolean = true): String {
    val firstNameField =
        if (firstOnly) {
            "namefirst"
        } else {
            "namegiven"
        }
    return """
        SELECT b.yearid AS year, 
               COALESCE(p.$firstNameField, 'unknown') || ' ' || COALESCE(p.namelast, 'unknown') AS playername,
               SUM(b.hr) AS season_hr
        FROM batting b, people p
        WHERE b.playerid = p.playerid
        GROUP BY playername, yearid
        ORDER BY season_hr desc
    """.trimIndent()
}

fun playerNameLengthSql(nameOption: String): String {
    val namelengthValue =
        when (nameOption) {
            "checkFirst" -> "namefirst"
            "checkFirstLast" -> "namefirst||namelast"
            "checkFull" -> "namegiven||namelast"
            else -> "namelast"
        }

    return """
        SELECT
            COALESCE(p.namegiven, 'unknown') || ' ' || COALESCE(p.namelast, 'unknown')                     as name,
            COALESCE(p.birthyear, 0) || '-' || COALESCE(p.birthmonth, 0) || '-' || COALESCE(p.birthday, 0) as born,
            COALESCE(p.debut, 'unknown')                                                                   as debut,
            COALESCE(p.finalgame, 'unknown')                                                               as finalgame,
            COALESCE(p.playerid, 'unknown')                                                                as playerid,
            COALESCE(p.bbrefid, 'unknown')                                                                 as bbrefid,
            COALESCE(TO_CHAR(m.playermanager), '0') as playerManager
        FROM PEOPLE p
        LEFT JOIN (
                SELECT playerid, CASE WHEN PLYRMGR = 'Y' THEN 1 ELSE 0 END as playermanager
                FROM MANAGERS
                GROUP BY PLAYERID, playermanager
                HAVING SUM(playermanager) >= 1
        ) m
        ON p.PLAYERID = m.PLAYERID
        WHERE LENGTH($namelengthValue) = ?
    """.trimIndent()
}

val minMaxNameLengthsSql =
    """
    select
        min(length(namefirst)) as minFName, max(length(namefirst)) as maxFName,
        min(length(namelast)) as minLName, max(length(namelast)) as maxLName,
        min(length(namefirst||namelast)) as minName, max(length(namefirst||namelast)) as maxName,
        min(length(namegiven||namelast)) as minFull, max(length(namegiven||namelast)) as maxFull
    from PEOPLE;
    """.trimIndent()
