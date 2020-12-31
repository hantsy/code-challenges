import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun String.toLocalDateTime(): LocalDateTime = this.toLocalDateTime(null)

fun String.toLocalDateTime(defaultFormatter: DateTimeFormatter?): LocalDateTime =
    LocalDateTime.parse(this, defaultFormatter ?: DateTimeFormatter.ofPattern(Constants.DEFAULT_DATETIME_FORMATTER))