import java.math.BigDecimal
import java.time.LocalDateTime

//AKNBVHMN, 20/08/2020 13:14:11, 10.95, Kwik-E-Mart, REVERSAL, YGXKOEIA
data class Transaction(
    val id: String,
    val transactedAt: LocalDateTime,
    val amount: BigDecimal,
    val merchantName: String,
    val type: TransactionType,
    val relatedTransactionId: String?
)