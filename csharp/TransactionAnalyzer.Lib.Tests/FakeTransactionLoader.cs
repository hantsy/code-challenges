using System;
using System.Globalization;

namespace TransactionAnalyzer.Lib.Tests
{
    internal partial class FakeTransactionLoader : ITransactionLoader
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
            return Fixtures.Load();
        }
    }
}