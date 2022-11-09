import data.TodoCategory
import data.TodoItem
import edu.uwaterloo.cs.todo.lib.ItemImportance
import exceptions.IdNotFoundException
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import sync.SyncService

internal class DeleteItemTest: CommandTest() {

    @Test
    fun nonExistItemNumber_ThrowIdNotFoundException() {
        // Arrange
        val client = HttpClient(CIO)
        val syncService = SyncService(client, false)
        val command = DeleteItem(dataFactory, syncService)

        //Act & Assert
        assertThrowsExactly(IdNotFoundException::class.java) { command.parse(arrayOf("1")) }
    }

    @Test
    fun deleteSuccess_CategoryAndItemAllMatch() {
        // Arrange
        val client = HttpClient(CIO)
        val syncService = SyncService(client, false)
        val command = DeleteItem(dataFactory, syncService)

        dataFactory.transaction {
            val category = TodoCategory.new {
                name = "Physics"
                favoured = true
            }
            TodoItem.new {
                name = "A1"
                importance = ItemImportance.NORMAL
                categoryId = category.uniqueId
                favoured = false
                description = String()
            }
            assertDoesNotThrow { command.parse(arrayOf(("1"))) }
            assertNull(TodoItem.findById(1))
        }
    }
}


