package data

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection

class DataFactory(private val url: String = "jdbc:sqlite:file:test?mode=memory&cache=shared") {
    private val database: Database

    init {
        database = connect()
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        setupDatabase()
    }

    protected fun finalize() {
        TransactionManager.closeAndUnregister(database)
    }

    private fun connect(): Database {
        val config = HikariConfig()
        config.jdbcUrl = url
        config.driverClassName = "org.sqlite.JDBC"
        config.validate()

        return Database.connect(HikariDataSource(config))
    }

    fun clearDatabase() {
        transaction { SchemaUtils.drop(TodoCategories, TodoItems, inBatch = true) }
    }

    fun setupDatabase() {
        transaction { SchemaUtils.createMissingTablesAndColumns(TodoCategories, TodoItems) }
    }

    fun <T> transaction(block: () -> T): T =
        org.jetbrains.exposed.sql.transactions.transaction(db = database) { block() }
}