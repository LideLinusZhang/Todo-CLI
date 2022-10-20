import edu.todo.lib.Owner
import edu.todo.lib.TodoCategory
import edu.todo.lib.TodoItem
import org.jetbrains.exposed.sql.Database

val database = Database.connect("jdbc:sqlite:./data.db", "org.sqlite.JDBC")

//TODO
val categories: MutableList<TodoCategory> = mutableListOf()
val items: MutableList<TodoItem> = mutableListOf()
val owner: Owner = Owner(String())