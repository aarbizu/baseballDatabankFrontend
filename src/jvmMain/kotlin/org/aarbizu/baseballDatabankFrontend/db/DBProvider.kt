package org.aarbizu.baseballDatabankFrontend.db

import java.sql.Connection

interface DBProvider {
    fun getConnection(): Connection
    fun stats()
}
