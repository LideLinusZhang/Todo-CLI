import data.DataFactory
import data.TodoCategories
import data.TodoCategory
import data.TodoItem
import exceptions.IdNotFoundException
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import sync.SyncService

internal class DeleteItemTest {

    @Test
    fun nonExistItemNumber_ThrowIdNotFoundException() {
        // Arrange
        val dataFactory = DataFactory()
        val client = HttpClient(CIO)
        val syncService = SyncService(client, false)
        val command = DeleteItem(dataFactory, syncService)

        //Act & Assert
        assertThrowsExactly(IdNotFoundException::class.java) { command.parse(arrayOf("1")) }
    }

    @Test
    fun DeleteSuccess_CategoryAndItemAllMatch() {
        // Arrange
        val dataFactory = DataFactory()
        val command = DeleteItem(dataFactory)


       dataFactory.transaction {
           val category = TodoCategory.new {
               name = "Physics"
               favoured = true
           }
           TodoItem.new {
               name = "A1"
               categoryId = category.uniqueId
           }
           assertDoesNotThrow { command.parse(arrayOf(("1")))}
           assertNull(TodoItem.findById(1))
       }


    }
}