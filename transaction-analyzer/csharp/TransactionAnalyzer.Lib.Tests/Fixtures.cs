using System;
using System.Globalization;

namespace TransactionAnalyzer.Lib.Tests;

internal class Fixtures
{
    public static Transaction[] Load()
    {
        return new Transaction[]
        {
            new()
            {
                Id = "WLMFRDGD",
                TransactedAt = DateTime.ParseExact("20/08/2020 12:45:33", "dd/MM/yyyy HH:mm:ss",
                    CultureInfo.InvariantCulture,
                    DateTimeStyles.None),
                Amount = new decimal(59.99),
                MerchantName = "Kwik-E-Mart",
                Type = TransactionType.PAYMENT,
                RelatedTransactionId = ""
            },
            new()
            {
                Id = "YGXKOEIA",
                TransactedAt = DateTime.ParseExact("20/08/2020 12:46:17", "dd/MM/yyyy HH:mm:ss",
                    CultureInfo.InvariantCulture,
                    DateTimeStyles.None),
                Amount = new decimal(10.95),
                MerchantName = "Kwik-E-Mart",
                Type = TransactionType.PAYMENT,
                RelatedTransactionId = ""
            },

            new()
            {
                Id = "LFVCTEYM",
                TransactedAt = DateTime.ParseExact("20/08/2020 12:50:02", "dd/MM/yyyy HH:mm:ss",
                    CultureInfo.InvariantCulture,
                    DateTimeStyles.None),
                Amount = new decimal(5.00),
                MerchantName = "MacLaren",
                Type = TransactionType.PAYMENT,
                RelatedTransactionId = ""
            },

            new()
            {
                Id = "SUOVOISP",
                TransactedAt = DateTime.ParseExact("20/08/2020 13:12:22", "dd/MM/yyyy HH:mm:ss",
                    CultureInfo.InvariantCulture,
                    DateTimeStyles.None),
                Amount = new decimal(5.00),
                MerchantName = "Kwik-E-Mart",
                Type = TransactionType.PAYMENT,
                RelatedTransactionId = ""
            },

            new()
            {
                Id = "AKNBVHMN",
                TransactedAt = DateTime.ParseExact("20/08/2020 13:14:11", "dd/MM/yyyy HH:mm:ss",
                    CultureInfo.InvariantCulture,
                    DateTimeStyles.None),
                Amount = new decimal(10.95),
                MerchantName = "Kwik-E-Mart",
                Type = TransactionType.REVERSAL,
                RelatedTransactionId = "YGXKOEIA"
            },

            new()
            {
                Id = "JYAPKZFZ",
                TransactedAt = DateTime.ParseExact("20/08/2020 14:07:10", "dd/MM/yyyy HH:mm:ss",
                    CultureInfo.InvariantCulture,
                    DateTimeStyles.None),
                Amount = new decimal(99.50),
                MerchantName = "MacLaren",
                Type = TransactionType.PAYMENT,
                RelatedTransactionId = ""
            }
        };
    }
}