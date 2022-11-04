import data.TodoCategory
import exceptions.IdNotFoundException
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import sync.SyncService

internal class DeleteCategoryTest: CommandTest() {
    @Test
    fun nonExistCategory_ThrowIdNotFoundException() {
        // Arrange
        val client = HttpClient(CIO)
        val syncService = SyncService(client, false)
        val command = DeleteCategory(dataFactory, syncService)

        //Act & Assert
        assertThrowsExactly(IdNotFoundException::class.java) { command.parse(arrayOf("1")) }

        //Cleanup
    }

    @Test
    fun deleteSuccess_CategoryMatch() {
        // Arrange
        val client = HttpClient(CIO)
        val syncService = SyncService(client, false)
        val command = DeleteCategory(dataFactory, syncService)

        dataFactory.transaction {
            TodoCategory.new {
                name = "Physics"
                favoured = true
            }
        }

        //Act & Assert
        assertDoesNotThrow { command.parse(arrayOf(("1"))) }
        dataFactory.transaction { assertNull(TodoCategory.findById(1)) }

        //Cleanup
    }
}


