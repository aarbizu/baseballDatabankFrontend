package org.aarbizu.baseballDatabankFrontend.config

import org.aarbizu.baseballDatabankFrontend.db.DBProvider
import org.aarbizu.baseballDatabankFrontend.db.DefaultDbProvider
import org.h2.jdbcx.JdbcConnectionPool
import org.slf4j.LoggerFactory
import java.sql.Connection

interface AppConfig {
    val port: Int
    val csvHome: String
    val jdbcUrl: String
    val db: DBProvider
}

object ServerConfig : AppConfig {
    override val port = System.getenv("PORT")?.toInt() ?: 8080
    override val csvHome = "src/commonMain/resources/csv"
    override val jdbcUrl = "jdbc:h2:mem:stats;DB_CLOSE_DELAY=-1"
    override val db = object : DefaultDbProvider(ServerConfig) {
        private val connectionPool by lazy { JdbcConnectionPool.create(jdbcUrl, "stats", "stats") }

        override fun getConnection(): Connection = connectionPool.connection

        override fun stats() {
            val logStats = LoggerFactory.getLogger("DB")
            logStats.info("active connections: ${connectionPool.activeConnections}");
        }
    }
}