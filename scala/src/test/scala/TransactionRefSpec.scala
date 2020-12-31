import org.scalatest.refspec.RefSpec

import java.time.LocalDateTime
import scala.collection.immutable.Set

class TransactionRefSpec extends RefSpec {
  val  transaction = Transaction(
      id = "test",
      transactedAt = LocalDateTime.now(),
      amount = BigDecimal(5.99),
      merchantName = "testMerchant",
      `type` = TransactionType.PAYMENT,
      relatedTransactionId = None
      )
  object `A Set` {

    object `when empty` {
      def `should have size 0` {
        assert {
          transaction.id eq "test"
          transaction.transactedAt isBefore(LocalDateTime.now())
          transaction.amount eq BigDecimal(5.99)
          transaction.merchantName eq "testMerchant"
          transaction.`type` eq TransactionType.PAYMENT
        }
      }

      def `should produce NoSuchElementException when head is invoked` {
        assertThrows[NoSuchElementException] {
          transaction.relatedTransactionId.get
        }
      }
    }

  }

}
