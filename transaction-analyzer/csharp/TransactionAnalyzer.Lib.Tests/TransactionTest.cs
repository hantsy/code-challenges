using System;
using Xunit;

namespace TransactionAnalyzer.Lib.Tests;

public class TransactionTest
{
    [Fact]
    public void Verify_Transaction()
    {
        var tran = new Transaction
        {
            Id = "test",
            TransactedAt = DateTime.Now,
            Amount = new decimal(9.00),
            MerchantName = "testMerchant",
            Type = TransactionType.PAYMENT,
            RelatedTransactionId = ""
        };
        Assert.Equal("test", tran.Id);
        Assert.Equal("testMerchant", tran.MerchantName);
        Assert.StrictEqual(new decimal(9.00), tran.Amount);
        Assert.True(tran.TransactedAt < DateTime.Now);
        Assert.True("" == tran.RelatedTransactionId);
    }
}