import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.format.DateTimeFormatter
import kotlin.test.assertNotNull

class StringToLocalDateTimeTest {

    @Test
    fun `valid string to LocalDateTime`() {
        assertNotNull("20/08/2020 12:45:33".toLocalDateTime())
    }

    @Test
    fun `custom valid string to LocalDateTime`() {
        assertNotNull("20-08-2020 12:45:33".toLocalDateTime(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
    }

    @Test
    fun `invalid string to LocalDateTime`() {
        assertThrows<Exception> {  "20-08-2020 12:45:33".toLocalDateTime()}
    }
}