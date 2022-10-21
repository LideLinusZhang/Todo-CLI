package data

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection

class DataFactory {
    init {
        connect()
        createSchema()
    }

    private fun connect() {
        Database.connect("jdbc:sqlite:./data.db", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }

    private fun createSchema() {
        org.jetbrains.exposed.sql.transactions.transaction {
            SchemaUtils.createMissingTablesAndColumns(TodoCategories, TodoItems)
        }
    }

    fun <T> transaction(block: () -> T): T = org.jetbrains.exposed.sql.transactions.transaction { block() }
}