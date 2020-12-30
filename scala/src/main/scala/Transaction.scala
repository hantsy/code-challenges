import TransactionType.TransactionType

import java.time.LocalDateTime

//AKNBVHMN, 20/08/2020 13:14:11, 10.95, Kwik-E-Mart, REVERSAL, YGXKOEIA
case class Transaction(
                        id: String,
                        transactedAt: LocalDateTime,
                        amount: BigDecimal,
                        merchantName: String,
                        `type`: TransactionType,
                        relatedTransactionId: Option[String])
