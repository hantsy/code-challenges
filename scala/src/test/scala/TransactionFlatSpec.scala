import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec

import java.time.LocalDateTime

class TransactionFlatSpec extends AnyFlatSpec with BeforeAndAfter {

  var transaction = Transaction(
    id = "test",
    transactedAt = LocalDateTime.now(),
    amount = BigDecimal(5.99),
    merchantName = "testMerchant",
    `type` = TransactionType.PAYMENT,
    relatedTransactionId = None
  )

  before {
    println("before...")
  }

  after {
    println("after...")
  }

  "A transaction instance " should " id equals test" in {
    assert {
      transaction.id eq "test"
      transaction.transactedAt isBefore (LocalDateTime.now())
      transaction.amount eq BigDecimal(5.99)
      transaction.merchantName eq "testMerchant"
      transaction.`type` eq TransactionType.PAYMENT
    }
  }

  it should "related transaction id is not set" in {
    assertThrows[NoSuchElementException] {
      transaction.relatedTransactionId.get
    }
  }
}
