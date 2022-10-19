package data

import edu.todo.lib.ItemImportance
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class TodoItem(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TodoItem>(TodoItems)

    var uniqueId by TodoItems.uniqueId
    var name by TodoItems.name
    var description by TodoItems.description
    var importance: ItemImportance by TodoItems.importance.transform(
        { it.ordinal }, { ItemImportance.values()[it] }
    )
    var deadline: LocalDate by TodoItems.deadline.transform(
        { it.toEpochDays() },
        { LocalDate.fromEpochDays(it) }
    )
}