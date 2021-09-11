import org.scalatest.funsuite.AnyFunSuite

import java.time.LocalDateTime

class TransactionFunSuite extends AnyFunSuite {

  val transaction = Transaction(
    id = "test",
    transactedAt = LocalDateTime.now(),
    amount = BigDecimal(5.99),
    merchantName = "testMerchant",
    `type` = TransactionType.PAYMENT,
    relatedTransactionId = None
  )

  test("A transaction instance should be verified easily") {
    assert {
      transaction.id eq "test"
      transaction.transactedAt isBefore (LocalDateTime.now())
      transaction.amount eq BigDecimal(5.99)
      transaction.merchantName eq "testMerchant"
      transaction.`type` eq TransactionType.PAYMENT
    }
  }

  test("retrieve relatedTransactionId should produce NoSuchElementException") {
    assertThrows[NoSuchElementException] {
      transaction.relatedTransactionId.get
    }
  }
}
