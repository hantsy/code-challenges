using System;
using System.Linq;

namespace TransactionAnalyzer.Lib
{
    public class TransactionRepository
    {
        private readonly Transaction[] _data;

        public TransactionRepository(ITransactionLoader loader)
        {
            _data = loader.Load();
        }

        public Transaction[] QueryByMerchantAndDateRange(
            string merchant,
            DateTime fromDate,
            DateTime toDate
        )
        {
            var reversalRelatedIds = _data
                .Where(s => s.Type == TransactionType.REVERSAL)
                .Select(s => s.RelatedTransactionId);

            return _data.Where(s => s.MerchantName.Equals(merchant)
                                    && s.TransactedAt > fromDate
                                    && s.TransactedAt < toDate
                                    && s.Type == TransactionType.PAYMENT
                                    && !reversalRelatedIds.Contains(s.Id)
            ).ToArray();
        }
    }
}