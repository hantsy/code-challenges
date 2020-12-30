import TransactionLoader.defaultDatetimePattern

import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.io.Source

class TransactionLoader(source: InputStream) {

  def load(): List[Transaction] = {
    val reader = Source.fromInputStream(this.source)
    reader.getLines().drop(1).map(buildTransaction).toList
  }

  private def buildTransaction(line: String): Transaction = {
    println("reading line:" + line)
    val fields: Array[String] = line.split(",")
    Transaction(
      fields(0).trim,
      LocalDateTime.parse(fields(1).trim, DateTimeFormatter.ofPattern(defaultDatetimePattern)),
      BigDecimal(fields(2).trim),
      fields(3).trim,
      TransactionType.withName(fields(4).trim),
      if (fields.length == 6) Some(fields(5).trim) else None
    )
  }

}

object TransactionLoader {
  val defaultDatetimePattern ="dd/MM/yyyy HH:mm:ss"
}
