import java.time.LocalDateTime

class TransactionRepository(loader: TransactionLoader) {
  var data: List[Transaction] = loader.load()

  def queryByMerchantAndDateRange(merchant: String, fromDate: LocalDateTime, toDate: LocalDateTime): List[Transaction] = {
    val reversalRelatedTransactionIds: List[String] = data.filter(_.`type` eq TransactionType.REVERSAL)
      .map(_.relatedTransactionId)
      .filter(_.isDefined)
      .map(_.get)

    data.filter((it: Transaction) => it.merchantName == merchant
      && it.transactedAt.isAfter(fromDate)
      && it.transactedAt.isBefore(toDate)
      && (it.`type` ne TransactionType.REVERSAL)
      && !reversalRelatedTransactionIds.contains(it.id)
    )
  }
}
