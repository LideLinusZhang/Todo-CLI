import data.DataFactory
import exceptions.IdNotFoundException
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class DeleteItemTest {

    @Test
    fun parse() {
        // Arrange
        val dataFactory = DataFactory()
        val command = DeleteItem(dataFactory)

        //Act & Assert
        assertThrowsExactly(IdNotFoundException::class.java) { command.parse(arrayOf("1")) }
    }
}