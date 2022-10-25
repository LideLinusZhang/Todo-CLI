package data

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection

class DataFactory(private val url: String = "jdbc:sqlite:file:test?mode=memory&cache=shared") {
    init {
        connect()
        createSchema()
    }

    private fun connect() {
        val config = HikariConfig()
        config.jdbcUrl = url
        config.driverClassName = "org.sqlite.JDBC"
        config.validate()

        Database.connect(HikariDataSource(config))
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }

    private fun createSchema() {
        org.jetbrains.exposed.sql.transactions.transaction {
            SchemaUtils.createMissingTablesAndColumns(TodoCategories, TodoItems)
        }
    }

    fun <T> transaction(block: () -> T): T = org.jetbrains.exposed.sql.transactions.transaction { block() }
}