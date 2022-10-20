import org.jetbrains.exposed.sql.Database

val database = Database.connect("jdbc:sqlite:./data.db", "org.sqlite.JDBC")