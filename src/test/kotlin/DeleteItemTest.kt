import data.DataFactory
import exceptions.IdNotFoundException
import io.ktor.client.*
import io.ktor.client.engine.cio.*
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
}