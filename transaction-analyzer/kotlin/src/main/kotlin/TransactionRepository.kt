import java.time.LocalDateTime

class TransactionRepository(val loader: TransactionLoader) {

    fun queryByMerchantAndDateRange(
        merchant: String,
        fromDate: LocalDateTime,
        toDate: LocalDateTime
    ): List<Transaction> {
        val data: List<Transaction> = loader.load()
        val reversal = data.filter { it.type == TransactionType.REVERSAL }
            .map { it.relatedTransactionId }
        return data.filter {
            it.merchantName == merchant
                    && it.transactedAt.isAfter(fromDate)
                    && it.transactedAt.isBefore(toDate)
                    && it.type == TransactionType.PAYMENT
                    && !reversal.contains(it.id)
        }
    }

}