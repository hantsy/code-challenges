import java.time.format.DateTimeFormatter

object Constants {
  final val defaultDatetimePattern = "dd/MM/yyyy HH:mm:ss"
  final val dateTimeFormatter = DateTimeFormatter.ofPattern(defaultDatetimePattern)
}
