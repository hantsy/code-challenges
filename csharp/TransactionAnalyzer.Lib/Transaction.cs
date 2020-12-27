#nullable enable
using System;

namespace TransactionAnalyzer.Lib
{
    public record Transaction(
        string Id,
        DateTime TransactedAt,
        decimal Amount,
        string MerchantName,
        TransactionType Type,
        string? RelatedTransactionId
    )
    {
    }
}