#nullable enable
using System;

namespace TransactionAnalyzer.Lib
{
    public record Transaction
    {
        public string Id { get; init; }
        public DateTime TransactedAt { get; init; }
        public decimal Amount { get; init; }
        public string MerchantName { get; init; }
        public TransactionType Type { get; init; }
        public string? RelatedTransactionId { get; init; }

        // public Transaction(
        //     string id,
        //     DateTime transactedAt,
        //     decimal amount,
        //     string merchantName,
        //     TransactionType type,
        //     string? relatedTransactionId=""
        // ) => (Id,
        //         TransactedAt,
        //         Amount,
        //         MerchantName,
        //         Type,
        //         RelatedTransactionId)
        //     = (id,
        //         transactedAt,
        //         amount,
        //         merchantName,
        //         type,
        //         relatedTransactionId);


    }
}