package org.aarbizu.baseballDatabankFrontend.config

const val dbPortNumber: Int = 5432
const val dbName: String = "stats"
const val dbHostname: String = "localhost"
const val dbUser: String = "postgres"
const val dbPassword: String = "changeme"
const val dbUrl = "jdbc:postgresql://$dbHostname:$dbPortNumber/$dbName"
