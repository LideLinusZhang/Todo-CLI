package data

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import java.util.UUID

object TodoItems: IntIdTable(columnName = "Id") {
    val uniqueId: Column<UUID> = uuid("UniqueId").uniqueIndex()
    val name: Column<String> = text("Name")
    val description: Column<String> = text("Description")
    val importance: Column<Int> = integer("Importance")
    val deadline: Column<Int> = integer("Deadline")
    val category = reference("categoryId", TodoCategories)
}