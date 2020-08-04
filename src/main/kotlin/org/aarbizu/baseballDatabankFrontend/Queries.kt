package org.aarbizu.baseballDatabankFrontend

abstract class Bind<T>(private val field: String, open val value: T) {
    override fun toString(): String {
        return "col=$field, value=$value"
    }
}

data class IntBind(private val field: String, override val value: Int) : Bind<Int>(field, value)
data class StrBind(private val field: String, override val value: String) : Bind<String>(field, value)

val playerNamesByLength = """
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

val playerLastNameSubstring = """
    SELECT
        COALESCE(namegiven, 'unknown') || ' ' || COALESCE(namelast, 'unknown') as name,
        COALESCE(birthyear, 0) || '-' || COALESCE(birthmonth, 0) || '-' || COALESCE(birthday, 0) as born,
        COALESCE(debut, 'unknown') as debut,
        COALESCE(finalgame, 'unknown') as finalgame
    FROM people
    WHERE LOWER(namelast) LIKE ?
    ORDER BY LENGTH(namelast) ASC
""".trimIndent()

fun playerNameRegex(first: Boolean = true, last: Boolean = true, caseSensitive: Boolean = false): String {
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
            COALESCE(finalgame, 'unknown') as finalgame
        FROM people
        WHERE $nameClauseColumn ${ if (caseSensitive) { "~" } else { "~*" }} ?
    """.trimIndent()
}
