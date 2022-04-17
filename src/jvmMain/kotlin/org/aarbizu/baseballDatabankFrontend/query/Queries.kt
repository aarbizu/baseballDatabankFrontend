package org.aarbizu.baseballDatabankFrontend.query

val playerNamesByLengthSql =
// COALESCE returns first non-null argument
"""
    SELECT
        COALESCE(namegiven, 'unknown') || ' ' || COALESCE(namelast, 'unknown') as name,
        COALESCE(birthyear, 0) || '-' || COALESCE(birthmonth, 0) || '-' || COALESCE(birthday, 0) as born,
        COALESCE(debut, 'unknown') as debut,
        COALESCE(finalgame, 'unknown') as finalgame,
        COALESCE(playerid, 'unknown') as playerid,
        COALESCE(bbrefid, 'unknown') as bbrefid
    FROM people
    WHERE LENGTH(namelast) = ?
    ORDER BY playerid
    """.trimIndent()

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
        ORDER BY season_hr desc;
    """.trimIndent()
}
