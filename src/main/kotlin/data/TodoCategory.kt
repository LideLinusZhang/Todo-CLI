package data

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class TodoCategory(id: EntityID<Int>): IntEntity(id), edu.uwaterloo.cs.todo.lib.TodoCategory {
    companion object : IntEntityClass<TodoCategory>(TodoCategories)

    override val uniqueId by TodoCategories.uniqueId
    override var name by TodoCategories.name
    override var favoured by TodoCategories.favoured
}