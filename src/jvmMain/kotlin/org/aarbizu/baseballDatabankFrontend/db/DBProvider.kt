package org.aarbizu.baseballDatabankFrontend.db

import org.aarbizu.baseballDatabankFrontend.config.AppConfig
import java.sql.Connection

interface DBProvider {
    fun getConnection(): Connection
    fun stats()
}

abstract class DefaultDbProvider(config: AppConfig) : DBProvider
