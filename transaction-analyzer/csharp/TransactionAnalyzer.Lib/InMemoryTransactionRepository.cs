using System;
using System.Linq;

namespace TransactionAnalyzer.Lib;

public class InMemoryTransactionRepository(ITransactionLoader loader) : ITransactionRepository
{
    private readonly Transaction[] _data = loader.Load();

    public Transaction[] QueryByMerchantAndDateRange(
        string merchant,
        DateTime fromDate,
        DateTime toDate
    )
    {
        var reversalRelatedIds = _data
            .Where(s => s.Type == TransactionType.Reversal)
            .Select(s => s.RelatedTransactionId);

        return _data.Where(s => s.MerchantName.Equals(merchant)
                                && s.TransactedAt > fromDate
                                && s.TransactedAt < toDate
                                && s.Type == TransactionType.Payment
                                && !reversalRelatedIds.Contains(s.Id)
        ).ToArray();
    }
}