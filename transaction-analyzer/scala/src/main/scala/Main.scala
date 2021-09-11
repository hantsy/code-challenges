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
  //parse and query
  val result: List[Transaction] = TransactionRepository(InputStreamTransactionLoader(input))
    .queryByMerchantAndDateRange(
      merchant,
      LocalDateTime.parse(fromDate, dateTimeFormatter),
      LocalDateTime.parse(toDate, dateTimeFormatter)
    )
  private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
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
    println(printTemplate.format(result.size, new DecimalFormat("#0.##").format(avg)))
  }

}
