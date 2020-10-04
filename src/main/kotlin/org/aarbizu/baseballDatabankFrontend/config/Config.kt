package org.aarbizu.baseballDatabankFrontend.config

import java.net.URI

const val dbPortNumber: Int = 5432
const val dbName: String = "stats"
const val dbHostname: String = "localhost"
const val dbUser: String = "postgres"
const val dbPassword: String = "changeme"
const val dbUrlEnv: String = "DATABASE_URL"
val localDbUri = getDbUri(dbUser, dbPassword, dbHostname, dbPortNumber, dbName)
val dbUri = URI(System.getenv(dbUrlEnv)?.toString() ?: localDbUri)

fun getDbUri(user: String, pass: String, host: String,
             port: Int, dbName: String): String = "postgres://$user:$pass@$host:$port/$dbName"