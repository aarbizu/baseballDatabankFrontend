package org.aarbizu.baseballDatabankFrontend.query

// COALESCE returns first non-null argument

val playerNamesByLengthSql = """
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

val playerLastNameSubstringSql = """
    SELECT
        COALESCE(namegiven, 'unknown') || ' ' || COALESCE(namelast, 'unknown') as name,
        COALESCE(birthyear, 0) || '-' || COALESCE(birthmonth, 0) || '-' || COALESCE(birthday, 0) as born,
        COALESCE(debut, 'unknown') as debut,
        COALESCE(finalgame, 'unknown') as finalgame,
        COALESCE(playerid, 'unknown') as playerid,
        COALESCE(bbrefid, 'unknown') as bbrefid
    FROM people
    WHERE LOWER(namelast) LIKE ?
    ORDER BY LENGTH(namelast) ASC
""".trimIndent()

fun playerNameRegexSql(first: Boolean = true, last: Boolean = true, caseSensitive: Boolean = false): String {
    val nameClauseColumn = if (first && last) {
        "namefirst || ' ' || namelast"
    } else if (first) {
        "namefirst"
    } else {
        "namelast"
    }

    return """
        SELECT 
            namefirst || ' ' || namelast as name,
            COALESCE(birthyear, 0) || '-' || COALESCE(birthmonth, 0) || '-' || COALESCE(birthday, 0) as born,
            COALESCE(debut, 'unknown') as debut,
            COALESCE(finalgame, 'unknown') as finalgame,
            COALESCE(playerid, 'unknown') as playerid,
            COALESCE(bbrefid, 'unknown') as bbrefid
        FROM people
        WHERE $nameClauseColumn ${
        if (caseSensitive) {
            "~"
        } else {
            "~*"
        }
    } ?
    """.trimIndent()
}
