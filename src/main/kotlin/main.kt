import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.math.BigDecimal
import java.math.MathContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors

fun main(args: Array<String>) {
    println("fromDate (dd/MM/yyyy HH:mm:ss):")
    val fromDate = readLine()
    println("toDate (dd/MM/yyyy HH:mm:ss):")
    val toDate = readLine()
    println("merchant:")
    val merchant = readLine()
    val input = object {}.javaClass.getResourceAsStream("input.csv")
    val result = TransactionRepository(TransactionLoader(input))
        .queryByMerchantAndDateRange(
            merchant!!,
            LocalDateTime.parse(fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
            LocalDateTime.parse(toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
        )

    if (result.isNotEmpty()) {
        println("Number of transactions = ${result.size}")
        val sum = result.sumOf { it.amount }/result.size.toBigDecimal()
        println("Average Transaction Value = ${sum.round(MathContext(2))}")
    } else {
        println("No transactions found.")
    }
}


class TransactionRepository(val loader: TransactionLoader) {
    var data: List<Transaction> = loader.load()
    fun queryByMerchantAndDateRange(
        merchant: String,
        fromDate: LocalDateTime,
        toDate: LocalDateTime
    ): List<Transaction> {
        val reversal = data.filter { it.type == TransactionType.REVERSAL }
            .map { it.relatedTransactionId }
        return data.filter {
            it.merchantName == merchant
                    && it.transactedAt.isAfter(fromDate)
                    && it.transactedAt.isBefore(toDate)
                    && it.type != TransactionType.REVERSAL
                    && !reversal.contains(it.id)
        }
    }

}

class TransactionLoader(val input: InputStream) {
    fun load(): List<Transaction> {
        val reader = BufferedReader(InputStreamReader(input))
        val lines = reader.lines().collect(Collectors.toList())
        return lines.subList(1, lines.size).map { buildTransaction(it) }
    }

    private fun buildTransaction(line: String): Transaction {
        println("build transaction from line: $line")
        val fields = line.split(",")
        return Transaction(
            fields[0].trim(),
            LocalDateTime.parse(fields[1].trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
            BigDecimal(fields[2].trim()),
            fields[3].trim(),
            TransactionType.valueOf(fields[4].trim()),
            fields[5].trim()
        )
    }
}

//AKNBVHMN, 20/08/2020 13:14:11, 10.95, Kwik-E-Mart, REVERSAL, YGXKOEIA
data class Transaction(
    val id: String,
    val transactedAt: LocalDateTime,
    val amount: BigDecimal,
    val merchantName: String,
    val type: TransactionType,
    val relatedTransactionId: String?
)

enum class TransactionType {
    PAYMENT,
    REVERSAL,
}
