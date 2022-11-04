import data.TodoCategory
import exceptions.IdNotFoundException
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrowsExactly
import org.junit.jupiter.api.Test
import sync.SyncService

internal class ModifyCategoryTest: CommandTest() {
    @Test
    fun nonExistCategory_ThrowIdNotFoundException() {
        // Arrange
        val client = HttpClient(CIO)
        val syncService = SyncService(client, false)
        val command = ModifyCategory(dataFactory, syncService)

        //Act & Assert
        assertThrowsExactly(IdNotFoundException::class.java) { command.parse(arrayOf("1", "--field", "name", "Math")) }
    }

    @Test
    fun modifyCategory_Successful() {
        // Arrange
        val client = HttpClient(CIO)
        val syncService = SyncService(client, false)
        val command = ModifyCategory(dataFactory, syncService)

        dataFactory.transaction {
            TodoCategory.new {
                name = "Physics"
                favoured = true
            }
        }

        //Act & Assert
        assertDoesNotThrow { command.parse(arrayOf("1", "--field", "name", "Math")) }
    }
}