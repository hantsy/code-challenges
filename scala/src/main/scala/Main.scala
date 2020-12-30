import TransactionType.TransactionType

import java.io.InputStream
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.io.StdIn.readLine

object Main extends App {

  println("fromDate (dd/MM/yyyy HH:mm:ss):")
  val fromDate: String = readLine()
  println("toDate (dd/MM/yyyy HH:mm:ss):")
  val toDate: String = readLine()
  println("merchant:")
  val merchant: String = readLine()
  //load file
  val input: InputStream = getClass.getResourceAsStream("input.csv")
  private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
  //parse and query
  val result: List[Transaction] = new TransactionRepository(new TransactionLoader(input))
    .queryByMerchantAndDateRange(
      merchant,
      LocalDateTime.parse(fromDate, dateTimeFormatter),
      LocalDateTime.parse(toDate, dateTimeFormatter)
    )
  //print result
  if (result.isEmpty) {
    println("No transactions found.")
  }
  else {
    val printTemplate: String =
      """
                    Number of transactions = %d
                    Average Transaction Value = %s
                    """
    val sum: BigDecimal = result.map(_.amount).sum
    val avg: BigDecimal = sum / BigDecimal(result.size)
    println(printTemplate.formatted(result.size, new DecimalFormat("#0.##").format(avg)))
  }

}
