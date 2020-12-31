import org.scalatest.freespec.AnyFreeSpec

import java.time.LocalDateTime

class TransactionFreeSpec extends AnyFreeSpec {
  var transaction = Transaction(
    id = "test",
    transactedAt = LocalDateTime.now(),
    amount = BigDecimal(5.99),
    merchantName = "testMerchant",
    `type` = TransactionType.PAYMENT,
    relatedTransactionId = None
  )

  "A transaction" - {
    "when is instantized" - {
      "should easy to verify" in {
        assert {
          transaction.id eq "test"
          transaction.transactedAt isBefore(LocalDateTime.now())
          transaction.amount eq BigDecimal(5.99)
          transaction.merchantName eq "testMerchant"
          transaction.`type` eq TransactionType.PAYMENT
        }
      }

      "should produce NoSuchElementException when relatedTransactionId is not set" in {
        assertThrows[NoSuchElementException] {
          transaction.relatedTransactionId.get
        }
      }
    }
  }
}
