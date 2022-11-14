import data.TodoCategories
import data.TodoCategory
import data.TodoItem
import data.TodoItems
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import sync.CloudService
import sync.CloudServiceConfig
import java.util.*

const val databaseFileName: String = "data.db"
const val configFileName: String = "config.json"
const val databaseConnectionString = "jdbc:sqlite:./$databaseFileName"

fun createCloudService(config: CloudServiceConfig): CloudService? {
    return if (config.enabled) {
        val client = if (config.userCredential === null) {
            HttpClient(CIO) { install(ContentNegotiation) { json() } }
        } else {
            HttpClient(CIO) {
                install(ContentNegotiation) { json() }
                install(Auth) {
                    digest {
                        realm = edu.uwaterloo.cs.todo.lib.realm
                        credentials {
                            DigestAuthCredentials(
                                username = config.userCredential.userName,
                                password = config.userCredential.password
                            )
                        }
                    }
                }
            }
        }

        CloudService(client, config.serverUrl!!)
    } else null
}

fun getItemById(byUUID: Boolean, itemId: String): TodoItem? {
    return if (byUUID)
        TodoItem.find { TodoItems.uniqueId eq UUID.fromString(itemId) }.firstOrNull()
    else TodoItem.findById(itemId.toInt())
}

fun getCategoryById(byUUID: Boolean, categoryId: String): TodoCategory? {
    return if (byUUID)
        TodoCategory.find { TodoCategories.uniqueId eq UUID.fromString(categoryId) }.firstOrNull()
    else TodoCategory.findById(categoryId.toInt())
}