import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

trait TransactionFixtures {
    val transactionData: List[Transaction] = List(
       Transaction("WLMFRDGD", LocalDateTime.parse("20/08/2020 12:45:33", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), BigDecimal(59.99), "Kwik-E-Mart", TransactionType.PAYMENT, None),
       Transaction("YGXKOEIA", LocalDateTime.parse("20/08/2020 12:46:17", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), BigDecimal(10.95), "Kwik-E-Mart", TransactionType.PAYMENT, None),
       Transaction("LFVCTEYM", LocalDateTime.parse("20/08/2020 12:50:02", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), BigDecimal(5.00), "MacLaren", TransactionType.PAYMENT, None),
       Transaction("SUOVOISP", LocalDateTime.parse("20/08/2020 13:12:22", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), BigDecimal(5.00), "Kwik-E-Mart", TransactionType.PAYMENT, None),
       Transaction("AKNBVHMN", LocalDateTime.parse("20/08/2020 13:14:11", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), BigDecimal(10.95), "Kwik-E-Mart", TransactionType.REVERSAL, Some("YGXKOEIA")),
       Transaction("JYAPKZFZ", LocalDateTime.parse("20/08/2020 14:07:10", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), BigDecimal(99.50), "MacLaren", TransactionType.PAYMENT, None)
    )
  }

