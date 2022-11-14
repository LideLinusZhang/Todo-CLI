package data

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection

class DataFactory(private val url: String = "jdbc:sqlite:file:test?mode=memory&cache=shared", doSetup: Boolean = true) {
    private val database: Database

    init {
        database = connect()
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        if (doSetup) setupDatabase()
    }

    protected fun finalize() {
        TransactionManager.closeAndUnregister(database)
    }

    private fun connect(): Database {
        val config = HikariConfig()
        config.jdbcUrl = url
        config.driverClassName = "org.sqlite.JDBC"

        return Database.connect(HikariDataSource(config))
    }

    fun setupDatabase() {
        transaction { SchemaUtils.createMissingTablesAndColumns(TodoCategories, TodoItems, withLogs = false) }
    }

    fun clearDatabase() {
        transaction { SchemaUtils.drop(TodoCategories, TodoItems, inBatch = true) }
    }

    fun <T> transaction(block: () -> T): T =
        org.jetbrains.exposed.sql.transactions.transaction(db = database) { block() }
}