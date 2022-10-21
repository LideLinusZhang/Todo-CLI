package data

import edu.uwaterloo.cs.todo.lib.ItemImportance
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class TodoItem(id: EntityID<Int>) : IntEntity(id), edu.uwaterloo.cs.todo.lib.TodoItem {
    companion object : IntEntityClass<TodoItem>(TodoItems)

    override val uniqueId by TodoItems.uniqueId
    override var name by TodoItems.name
    override var description by TodoItems.description
    override var importance: ItemImportance by TodoItems.importance.transform(
        { it.ordinal },
        { ItemImportance.values()[it] }
    )
    override var categoryId by TodoItems.categoryId
    override var deadline: LocalDate? by TodoItems.deadline.transform(
        { it?.toEpochDays() },
        { if(it === null) null else LocalDate.fromEpochDays(it) }
    )
}