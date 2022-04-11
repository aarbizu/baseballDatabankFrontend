package org.aarbizu.baseballDatabankFrontend.config

fun getDbUri(
    user: String,
    pass: String,
    host: String,
    port: Int,
    dbName: String
) = "postgres://$user:$pass@$host:$port/$dbName"

const val csvHome = "src/commonMain/resources/csv"
const val jdbcUrl = "jdbc:h2:mem:stats;DB_CLOSE_DELAY=-1"