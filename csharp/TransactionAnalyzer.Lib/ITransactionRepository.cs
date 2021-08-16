using System;

namespace TransactionAnalyzer.Lib
{
    public interface ITransactionRepository
    {
        Transaction[] QueryByMerchantAndDateRange(
            string merchant,
            DateTime fromDate,
            DateTime toDate
        );
    }
}