import org.scalatest.funsuite.AnyFunSuite

class TransactionTypeFunSuite extends AnyFunSuite {


  test(" 'PAYMENT' should be parsed as TransactionType.PAYMENT") {
    assert {
      TransactionType.withName("PAYMENT") eq TransactionType.PAYMENT
    }
  }

  test(" 'REVERSAL' should be parsed as TransactionType.REVERSAL") {
    assert {
      TransactionType.withName("REVERSAL") eq TransactionType.REVERSAL
    }
  }

  test(" 'UNKNOWN' should not be parsed") {
    assertThrows[NoSuchElementException] {
      TransactionType.withName("UNKNOWN")
    }
  }
}
