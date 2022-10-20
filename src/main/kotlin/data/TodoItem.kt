package data

import edu.uwaterloo.cs.todo.lib.ItemImportance
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class TodoItem(id: EntityID<Int>) : IntEntity(id), edu.uwaterloo.cs.todo.lib.TodoItem {
    companion object : IntEntityClass<TodoItem>(TodoItems)

    val category by TodoCategory backReferencedOn  TodoItems.category

    override val uniqueId by TodoItems.uniqueId
    override val name by TodoItems.name
    override val description by TodoItems.description
    override val importance: ItemImportance by TodoItems.importance.transform(
        { it.ordinal }, { ItemImportance.values()[it] }
    )
    override val categoryId = this.category.uniqueId
    override val deadline: LocalDate by TodoItems.deadline.transform(
        { it.toEpochDays() },
        { LocalDate.fromEpochDays(it) }
    )
}