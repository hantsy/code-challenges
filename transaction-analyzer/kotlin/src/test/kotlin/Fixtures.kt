import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Fixtures {
    val transactionData = listOf(
        Transaction(
            id = "WLMFRDGD",
            transactedAt = LocalDateTime.parse(
                "20/08/2020 12:45:33",
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            ),
            amount = BigDecimal.valueOf(59.99),
            merchantName = "Kwik-E-Mart",
            type = TransactionType.PAYMENT,
            relatedTransactionId = ""
        ),
        Transaction(
            id = "YGXKOEIA",
            transactedAt = LocalDateTime.parse(
                "20/08/2020 12:46:17",
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            ),
            amount = BigDecimal.valueOf(10.95),
            merchantName = "Kwik-E-Mart",
            type = TransactionType.PAYMENT,
            relatedTransactionId = ""
        ),
        Transaction(
            id = "LFVCTEYM",
            transactedAt = LocalDateTime.parse(
                "20/08/2020 12:50:02",
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            ),
            amount = BigDecimal.valueOf(5.00),
            merchantName = "MacLaren",
            type = TransactionType.PAYMENT,
            relatedTransactionId = ""
        ),
        Transaction(
            id = "SUOVOISP",
            transactedAt = LocalDateTime.parse(
                "20/08/2020 13:12:22",
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            ),
            amount = BigDecimal.valueOf(5.00),
            merchantName = "Kwik-E-Mart",
            type = TransactionType.PAYMENT,
            relatedTransactionId = ""
        ),
        Transaction(
            id = "AKNBVHMN",
            transactedAt = LocalDateTime.parse(
                "20/08/2020 13:14:11",
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            ),
            amount = BigDecimal.valueOf(10.95),
            merchantName = "Kwik-E-Mart",
            type = TransactionType.REVERSAL,
            relatedTransactionId = "YGXKOEIA"
        ),
        Transaction(
            id = "JYAPKZFZ",
            transactedAt = LocalDateTime.parse(
                "20/08/2020 14:07:10",
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            ),
            amount = BigDecimal.valueOf(99.50),
            merchantName = "MacLaren",
            type = TransactionType.PAYMENT,
            relatedTransactionId = ""
        )
    )

}