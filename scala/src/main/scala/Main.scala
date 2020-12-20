import TransactionType.TransactionType

import java.io.InputStream
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.io.Source
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
  val result: List[Transaction] = new TransactionRepository(new TransactionLoader(input))
    .queryByMerchantAndDateRange(
      merchant,
      LocalDateTime.parse(fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
      LocalDateTime.parse(toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
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

class TransactionRepository(loader: TransactionLoader) {
  var data: List[Transaction] = loader.load()

  def queryByMerchantAndDateRange(merchant: String, fromDate: LocalDateTime, toDate: LocalDateTime): List[Transaction] = {
    val reversal: List[String] = data.filter(_.`type` eq TransactionType.REVERSAL)
      .map(_.relatedTransactionId)

    data.filter((it: Transaction) => it.merchantName == merchant
      && it.transactedAt.isAfter(fromDate)
      && it.transactedAt.isBefore(toDate)
      && (it.`type` ne TransactionType.REVERSAL)
      && !reversal.contains(it.id)
    )
  }
}

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
      LocalDateTime.parse(fields(1).trim, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
      BigDecimal(fields(2).trim),
      fields(3).trim,
      TransactionType.withName(fields(4).trim),
      if (fields.length == 6) fields(5).trim else null
    )
  }

}

//AKNBVHMN, 20/08/2020 13:14:11, 10.95, Kwik-E-Mart, REVERSAL, YGXKOEIA
case class Transaction(
                        id: String,
                        transactedAt: LocalDateTime,
                        amount: BigDecimal,
                        merchantName: String,
                        `type`: TransactionType,
                        relatedTransactionId: String) {
}

object TransactionType extends Enumeration {
  type TransactionType = Value
  val PAYMENT = Value("PAYMENT")
  val REVERSAL = Value("REVERSAL")
}

