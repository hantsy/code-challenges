import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class TransactionRepository {

    private final TransactionLoader loader;

    TransactionRepository(TransactionLoader _loader) {
        loader = _loader;
    }

    public List<Transaction> queryByMerchantAndDateRange(
            String merchant,
            LocalDateTime fromDate,
            LocalDateTime toDate
    ) {
        List<Transaction> data;
        try {
            data = this.loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            data = Collections.emptyList();
        }
        var reversal = data.stream()
                .filter(it -> it.type() == TransactionType.REVERSAL)
                .map(Transaction::relatedTransactionId)
                .collect(Collectors.toList());
        return data.stream()
                .filter(it -> it.merchantName().equals(merchant)
                        && it.transactedAt().isAfter(fromDate)
                        && it.transactedAt().isBefore(toDate)
                        && it.type() == TransactionType.PAYMENT
                        && !reversal.contains(it.id())
                )
                .collect(Collectors.toList());
    }

}
