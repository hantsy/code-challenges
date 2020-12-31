import org.scalatest.wordspec.AnyWordSpec

import java.time.LocalDateTime

class TransactionWordSpec extends AnyWordSpec {
  val  transaction = Transaction(
    id = "test",
    transactedAt = LocalDateTime.now(),
    amount = BigDecimal(5.99),
    merchantName = "testMerchant",
    `type` = TransactionType.PAYMENT,
    relatedTransactionId = None
  )
  "A Set" when {
    "empty" should {
      "have size 0" in {
        assert {
          transaction.id eq "test"
          transaction.transactedAt isBefore(LocalDateTime.now())
          transaction.amount eq BigDecimal(5.99)
          transaction.merchantName eq "testMerchant"
          transaction.`type` eq TransactionType.PAYMENT
        }
      }

      "produce NoSuchElementException when retrieving relatedTransactionId " in {
        assertThrows[NoSuchElementException] {
          transaction.relatedTransactionId.get
        }
      }
    }
  }
}
