package org.aarbizu.baseballDatabankFrontend.config

import java.net.URI

const val dbPortNumber = 5432
const val dbName = "stats"
const val dbHostname = "localhost"
const val dbUser = "postgres"
const val dbPassword = "changeme"
const val dbUrlEnv = "DATABASE_URL"
val localDbUri = getDbUri(dbUser, dbPassword, dbHostname, dbPortNumber, dbName)
val dbUri = URI(System.getenv(dbUrlEnv)?.toString() ?: localDbUri)

fun getDbUri(
    user: String,
    pass: String,
    host: String,
    port: Int,
    dbName: String
) = "postgres://$user:$pass@$host:$port/$dbName"

const val csvHome = "src/main/resources/csv"
const val jdbcUrl = "jdbc:h2:mem:stats;DB_CLOSE_DELAY=-1"