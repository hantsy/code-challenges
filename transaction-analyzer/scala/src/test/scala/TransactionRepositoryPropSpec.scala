import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.propspec.AnyPropSpec

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class TransactionRepositoryPropSpec extends AnyPropSpec
  with MockFactory
  with TableDrivenPropertyChecks
  with should.Matchers
  with TransactionFixtures {

  val examples =
    Table("transactions",
      Tuple4(
        "Kwik-E-Mart",
        "20/08/2020 12:00:00",
        "20/08/2020 13:00:00",
        1
      ),
      Tuple4(
        "MacLaren",
        "20/08/2020 12:00:00",
        "20/08/2020 15:00:00",
        2
      )
    )
  private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

  property("query against fake loader") {
    val loader = FakeTransactionLoader()

    forAll(examples) { t =>
      val (merchant, fromDate, toDate, count) = t
      val result = TransactionRepository(loader)
        .queryByMerchantAndDateRange(merchant,
          LocalDateTime.parse(fromDate, dateTimeFormatter),
          LocalDateTime.parse(toDate, dateTimeFormatter)
        );
      assert {
        result.size == count
      }
    }
  }

  property("query against ScalaMock based loader") {
    val loader = mock[TransactionLoader]
    (loader.load _).expects().returning(transactionData).twice

    forAll(examples) { t =>
      val (merchant, fromDate, toDate, count) = t
      val result = TransactionRepository(loader)
        .queryByMerchantAndDateRange(merchant,
          LocalDateTime.parse(fromDate, dateTimeFormatter),
          LocalDateTime.parse(toDate, dateTimeFormatter)
        );
      assert {
        result.size == count
      }
    }
  }

}
