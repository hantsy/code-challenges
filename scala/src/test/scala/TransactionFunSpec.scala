import org.scalatest.funspec.AnyFunSpec

import java.time.LocalDateTime

class TransactionFunSpec extends AnyFunSpec {
  val transaction = Transaction(
    id = "test",
    transactedAt = LocalDateTime.now(),
    amount = BigDecimal(5.99),
    merchantName = "testMerchant",
    `type` = TransactionType.PAYMENT,
    relatedTransactionId = None
  )

  describe("A transaction instance") {

    it("verify the properties") {
      assert {
        transaction.id eq "test"
        transaction.transactedAt isBefore (LocalDateTime.now())
        transaction.amount eq BigDecimal(5.99)
        transaction.merchantName eq "testMerchant"
        transaction.`type` eq TransactionType.PAYMENT
      }
    }

    it("should raise NoSuchElementException when relatedTransactionId is not set.") {
      assertThrows[NoSuchElementException] {
        transaction.relatedTransactionId.get
      }
    }
  }

}
