import commands.ListCategories
import data.TodoCategory
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test

internal class ListCategoryTest : CommandTest() {
    @Test
    fun listSuccess_ShowAllCategory() {
        // Arrange
        val command = ListCategories(dataFactory)

        dataFactory.transaction {
            TodoCategory.new {
                name = "Physics"
                favoured = true
            }
            assertDoesNotThrow { command.parse(arrayOf()) }
        }
    }
}


