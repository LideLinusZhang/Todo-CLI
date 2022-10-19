package data

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class TodoCategory(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<TodoCategory>(TodoCategories)

    val uniqueId by TodoCategories.uniqueId
    val name by TodoCategories.name
    val favoured by TodoCategories.favoured
}