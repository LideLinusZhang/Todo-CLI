import data.DataFactory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

internal abstract class CommandTest {
    protected val dataFactory = DataFactory()

    @BeforeEach
    protected fun setupDataFactory() {
        dataFactory.setupDatabase()
    }

    @AfterEach
    protected fun cleanupDataFactory() {
        dataFactory.clearDatabase()
    }
}