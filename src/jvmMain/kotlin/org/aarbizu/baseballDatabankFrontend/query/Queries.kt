package org.aarbizu.baseballDatabankFrontend.query

const val basePlayerSqlSegment =
    """
    SELECT
        COALESCE(p.namefirst, 'unknown')                                                               as first,
        COALESCE(p.namelast, 'unknown')                                                                as last,
        COALESCE(p.namegiven, 'unknown')                                                               as given,
        COALESCE(p.namegiven, 'unknown') || ' ' || COALESCE(p.namelast, 'unknown')                     as name,
        COALESCE(p.birthyear, 0) || '-' || COALESCE(p.birthmonth, 0) || '-' || COALESCE(p.birthday, 0) as born,
        COALESCE(p.debut, 'unknown')                                                                   as debut,
        COALESCE(p.finalgame, 'unknown')                                                               as finalgame,
        COALESCE(p.playerid, 'unknown')                                                                as playerid,
        COALESCE(p.bbrefid, 'unknown')                                                                 as bbrefid,
        COALESCE(TO_CHAR(m.playermanager), '0')                                                        as playerManager
    FROM PEOPLE p
    LEFT JOIN (
            SELECT playerid, CASE WHEN PLYRMGR = 'Y' THEN 1 ELSE 0 END as playermanager
            FROM MANAGERS
            GROUP BY PLAYERID, playermanager
            HAVING SUM(playermanager) >= 1
    ) m
    ON p.PLAYERID = m.PLAYERID"""

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
        $basePlayerSqlSegment
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
        $basePlayerSqlSegment
        WHERE LENGTH(REPLACE($namelengthValue, ' ', '')) = ?
    """.trimIndent()
}

val minMaxNameLengthsSql =
    """
    select
        min(length(namefirst)) as minFName, max(length(replace(namefirst, ' ', ''))) as maxFName,
        min(length(namelast)) as minLName, max(length(replace(namelast, ' ', ''))) as maxLName,
        min(length(namefirst||namelast)) as minName, max(length(replace(namefirst||namelast, ' ', ''))) as maxName,
        min(length(namegiven||namelast)) as minFull, max(length(replace(namegiven||namelast, ' ', ''))) as maxFull
    from PEOPLE;
    """.trimIndent()

fun orderedByLengthSql(nameField: String): String {
    val name =
        when (nameField) {
            "First" -> "namefirst"
            "FirstLast" -> "namefirst||namelast"
            "Full" -> "namegiven||namelast"
            else -> "namelast"
        }

    return """
        SELECT
            p.namefirst                                                               as first,
            p.namelast                                                                as last,
            p.namegiven                                                               as given,
            p.namegiven || ' ' || COALESCE(p.namelast, 'unknown')                     as name,
            COALESCE(p.birthyear, 0) || '-' || COALESCE(p.birthmonth, 0) || '-' || COALESCE(p.birthday, 0) as born,
            COALESCE(p.debut, 'unknown')                                                                   as debut,
            COALESCE(p.finalgame, 'unknown')                                                               as finalgame,
            COALESCE(p.playerid, 'unknown')                                                                as playerid,
            COALESCE(p.bbrefid, 'unknown')                                                                 as bbrefid,
            COALESCE(TO_CHAR(m.playermanager), '0')                                                        as playerManager
        FROM PEOPLE p
        LEFT JOIN (
            SELECT playerid, CASE WHEN PLYRMGR = 'Y' THEN 1 ELSE 0 END as playermanager
            FROM MANAGERS
            GROUP BY PLAYERID, playermanager
            HAVING SUM(playermanager) >= 1
        ) m
        ON p.PLAYERID = m.PLAYERID
        WHERE p.namefirst IS NOT NULL
        AND p.namelast IS NOT NULL
        AND p.namegiven IS NOT NULL
        ORDER BY LENGTH(REPLACE(p.$name, ' ', '')) DESC
    """.trimIndent()
}

fun careerStatleader(statName: String, statTable: String): String {
    val statNameNormalized =
        when (statName) {
            "DOUBLE" -> "\"2B\""
            "TRIPLE" -> "\"3B\""
            else -> statName
        }
    val statClause =
        when (statName) {
            "IPOUTS" -> "TO_CHAR(sum(round($statNameNormalized/3.0, 1)),'999999.9')"
            "ERA" ->
                "TO_CHAR(round(sum(ER*9)/sum(case when IPOUTS > 0 then round(IPOUTS/3.0, 3) else 1 end), 2),'9.99')"
            else -> "sum($statNameNormalized)"
        }

    val havingClause =
        when (statName) {
            "ERA" -> "CAST(stat_total as float) > 0 AND SUM(IPOUTS) > 1000"
            else -> "CAST(stat_total as float) > 0"
        }

    val orderByClause =
        when (statName) {
            "ERA" -> "asc"
            else -> "desc"
        }

    return """
        SELECT
            b.playerid,
            COALESCE(p.namefirst,'unknown') || ' ' || COALESCE(p.namelast,'unknown') as name,
            $statClause as stat_total,
        FROM $statTable b, PEOPLE p
        WHERE b.PLAYERID = p.PLAYERID
        GROUP by b.PLAYERID
        HAVING $havingClause
        ORDER by stat_total $orderByClause
    """.trimIndent()
}
