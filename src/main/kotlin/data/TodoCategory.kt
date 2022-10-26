package data

import data.TodoCategories.clientDefault
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class TodoCategory(id: EntityID<Int>) : IntEntity(id), edu.uwaterloo.cs.todo.lib.TodoCategory {
    companion object : IntEntityClass<TodoCategory>(TodoCategories)

    override val uniqueId by TodoCategories.uniqueId.clientDefault { UUID.randomUUID() }
    override var name by TodoCategories.name
    override var favoured by TodoCategories.favoured
    override var modifiedTime: LocalDateTime by TodoCategories.modifiedTime
        .clientDefault { Clock.System.now().epochSeconds }
        .transform(
            { it.toInstant(TimeZone.currentSystemDefault()).epochSeconds },
            { Instant.fromEpochSeconds(it).toLocalDateTime(TimeZone.currentSystemDefault()) }
        )
}