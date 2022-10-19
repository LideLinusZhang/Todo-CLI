package data

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import java.util.UUID

object TodoCategories: IntIdTable(columnName = "Id") {
    val uniqueId: Column<UUID> = uuid("Id").uniqueIndex()
    val name: Column<String> = text("Name")
    val favoured: Column<Boolean> = bool("Favoured")
}