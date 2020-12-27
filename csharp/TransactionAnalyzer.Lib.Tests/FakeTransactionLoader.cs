using System;
using System.Globalization;

namespace TransactionAnalyzer.Lib.Tests
{
    internal class FakeTransactionLoader : ITransactionLoader
    {
        /*
         WLMFRDGD, 20/08/2020 12:45:33, 59.99, Kwik-E-Mart, PAYMENT,
        YGXKOEIA, 20/08/2020 12:46:17, 10.95, Kwik-E-Mart, PAYMENT,
        LFVCTEYM, 20/08/2020 12:50:02, 5.00, MacLaren, PAYMENT,
        SUOVOISP, 20/08/2020 13:12:22, 5.00, Kwik-E-Mart, PAYMENT,
        AKNBVHMN, 20/08/2020 13:14:11, 10.95, Kwik-E-Mart, REVERSAL, YGXKOEIA
            JYAPKZFZ, 20/08/2020 14:07:10, 99.50, MacLaren, PAYMENT,
            */
        public Transaction[] Load()
        {
            return new Transaction[]
            {
                new Transaction
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
                new Transaction
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

                new Transaction
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

                new Transaction
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

                new Transaction
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

                new Transaction
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
}