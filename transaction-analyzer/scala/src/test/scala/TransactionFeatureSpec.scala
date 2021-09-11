import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec

import java.time.LocalDateTime

class TransactionFeatureSpec extends AnyFeatureSpec with GivenWhenThen {

  info("As a TV set owner")
  info("I want to be able to turn the TV on and off")
  info("So I can watch TV when I want")
  info("And save energy when I'm not watching TV")

  Feature("TV power button") {
    Scenario("User presses power button when TV is off") {

      Given("a TV set that is switched off")
      var transaction = Transaction(
        id = "test",
        transactedAt = LocalDateTime.now(),
        amount = BigDecimal(5.99),
        merchantName = "testMerchant",
        `type` = TransactionType.PAYMENT,
        relatedTransactionId = None
      )
      assert {
        transaction.id eq "test"
        transaction.transactedAt isBefore (LocalDateTime.now())
        transaction.amount eq BigDecimal(5.99)
        transaction.merchantName eq "testMerchant"
        transaction.`type` eq TransactionType.PAYMENT
      }

      assertThrows[NoSuchElementException] {
        transaction.relatedTransactionId.get
      }

      //      When("the power button is pressed")
      //      tv.pressPowerButton()
      //
      //      Then("the TV should switch on")
      //      assert(tv.isOn)
    }

  }
}
