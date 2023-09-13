using System;
using System.Globalization;
using Moq;
using Xunit;

namespace TransactionAnalyzer.Lib.Tests;

public class TransactionRepositoryTest
{
    [Theory]
    [InlineData("20/08/2020 12:00:00", "20/08/2020 13:00:00", "Kwik-E-Mart", 1)]
    [InlineData("20/08/2020 12:00:00", "20/08/2020 15:00:00", "MacLaren", 2)]
    public void Verify_QueryByMerchantAndDateRange(string fromDate, string toDate, string merchant, int n)
    {
        var loader = new FakeTransactionLoader();
        var transactions = new InMemoryTransactionRepository(loader)
            .QueryByMerchantAndDateRange(
                merchant,
                DateTime.ParseExact(fromDate, "dd/MM/yyyy HH:mm:ss", CultureInfo.InvariantCulture,
                    DateTimeStyles.None),
                DateTime.ParseExact(toDate, "dd/MM/yyyy HH:mm:ss", CultureInfo.InvariantCulture,
                    DateTimeStyles.None)
            );
        Assert.Equal(n, transactions.Length);
    }

    [Theory]
    [InlineData("20/08/2020 12:00:00", "20/08/2020 13:00:00", "Kwik-E-Mart", 1)]
    [InlineData("20/08/2020 12:00:00", "20/08/2020 15:00:00", "MacLaren", 2)]
    public void Verify_QueryByMerchantAndDateRange_Mock(string fromDate, string toDate, string merchant, int n)
    {
        var mockLoader = new Mock<ITransactionLoader>();

        mockLoader.Setup(loader => loader.Load()).Returns(fakeLoadedData);
        var transactions = new InMemoryTransactionRepository(mockLoader.Object)
            .QueryByMerchantAndDateRange(
                merchant,
                DateTime.ParseExact(fromDate, "dd/MM/yyyy HH:mm:ss", CultureInfo.InvariantCulture,
                    DateTimeStyles.None),
                DateTime.ParseExact(toDate, "dd/MM/yyyy HH:mm:ss", CultureInfo.InvariantCulture,
                    DateTimeStyles.None)
            );
        Assert.Equal(n, transactions.Length);
    }

    /*
     WLMFRDGD, 20/08/2020 12:45:33, 59.99, Kwik-E-Mart, PAYMENT,
    YGXKOEIA, 20/08/2020 12:46:17, 10.95, Kwik-E-Mart, PAYMENT,
    LFVCTEYM, 20/08/2020 12:50:02, 5.00, MacLaren, PAYMENT,
    SUOVOISP, 20/08/2020 13:12:22, 5.00, Kwik-E-Mart, PAYMENT,
    AKNBVHMN, 20/08/2020 13:14:11, 10.95, Kwik-E-Mart, REVERSAL, YGXKOEIA
    JYAPKZFZ, 20/08/2020 14:07:10, 99.50, MacLaren, PAYMENT,
    */
    private Transaction[] fakeLoadedData()
    {
        return Fixtures.Load();
    }
}