import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.streams.toList

interface TransactionLoader {
    fun load(): List<Transaction>
}

internal class InputStreamTransactionLoader(val input: InputStream) : TransactionLoader {
    override fun load(): List<Transaction> {
        val reader = BufferedReader(InputStreamReader(input))
        return reader.lines().skip(1).map { buildTransaction(it) }.toList()
    }

    private fun buildTransaction(line: String): Transaction {
        println("build transaction from line: $line")
        val fields = line.split(",")
        return Transaction(
            id = fields[0].trim(),
            transactedAt = LocalDateTime.parse(
                fields[1].trim(),
                DateTimeFormatter.ofPattern(Constants.defaultDateTimeFormatter)
            ),
            amount = BigDecimal(fields[2].trim()),
            merchantName = fields[3].trim(),
            type = TransactionType.valueOf(fields[4].trim()),
            relatedTransactionId = fields[5].trim()
        )
    }
}