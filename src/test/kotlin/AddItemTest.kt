import com.github.ajalt.clikt.core.UsageError
import data.TodoCategory
import data.TodoItem
import edu.uwaterloo.cs.todo.lib.ItemImportance
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrowsExactly
import org.junit.jupiter.api.Test
import sync.SyncService

internal class AddItemTest: CommandTest() {
    @Test
    fun addItem_Successful() {
        // Arrange
        val client = HttpClient(CIO)
        val syncService = SyncService(client, false)
        val command = AddItem(dataFactory, syncService)

        dataFactory.transaction {
            TodoCategory.new {
                name = "Physics"
                favoured = true
            }
        }

        //Act & Assert
        assertDoesNotThrow { command.parse(arrayOf("--search-category-by", "id", "1", "A1")) }
    }

    @Test
    fun doubleItem_ThrowItemAlreadyExistException() {
        // Arrange
        val client = HttpClient(CIO)
        val syncService = SyncService(client, false)
        val command = AddItem(dataFactory, syncService)

        dataFactory.transaction {
            val category = TodoCategory.new {
                name = "Physics"
                favoured = true
            }
            TodoItem.new {
                name = "A1"
                importance = ItemImportance.NORMAL
                categoryId = category.uniqueId
                description = String()
            }
        }

        //Act & Assert
        assertThrowsExactly(UsageError::class.java) { command.parse(arrayOf("--search-category-by", "id", "1", "A1")) }
    }
}