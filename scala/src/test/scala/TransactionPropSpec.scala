import org.scalatest.matchers.should
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.propspec.AnyPropSpec

import java.time.LocalDateTime


class TransactionPropSpec extends AnyPropSpec with TableDrivenPropertyChecks with should.Matchers {

  val examples =
    Table("transactions",
      Transaction(
        id = "test",
        transactedAt = LocalDateTime.now(),
        amount = BigDecimal(5.99),
        merchantName = "testMerchant",
        `type` = TransactionType.PAYMENT,
        relatedTransactionId = None
      ),
      Transaction(
        id = "test2",
        transactedAt = LocalDateTime.now(),
        amount = BigDecimal(19.99),
        merchantName = "testMerchant2",
        `type` = TransactionType.PAYMENT,
        relatedTransactionId = None
      )
    )

  property("transactedAt should happen before the current timestamp") {
    forAll(examples) { transaction =>
      transaction.transactedAt isBefore (LocalDateTime.now())
    }
  }

  property("invoking get on relatedTransactionId should produce NoSuchElementException") {
    forAll(examples) { transaction =>
      a[NoSuchElementException] should be thrownBy {
        transaction.relatedTransactionId.get
      }
    }
  }
}
