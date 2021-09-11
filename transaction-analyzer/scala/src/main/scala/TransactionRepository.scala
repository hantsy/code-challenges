import java.time.LocalDateTime

trait TransactionRepository {
  def queryByMerchantAndDateRange(merchant: String, fromDate: LocalDateTime, toDate: LocalDateTime): List[Transaction]
}
