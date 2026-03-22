using System;
using Xunit;

namespace TransactionAnalyzer.Lib.Tests;

public class TransactionTypeTest
{
    [Theory]
    [InlineData("PAYMENT", TransactionType.Payment)]
    [InlineData("REVERSAL", TransactionType.Reversal)]
    public void ParseValidStringToTransactionType_ReturnTrue(string value, TransactionType expected)
    {
        Enum.TryParse(value, true, out TransactionType parsedType);
        Assert.Equal(expected, parsedType);
    }

    [Theory]
    [InlineData("PAYMENT1")]
    [InlineData("REVERSAL2")]
    public void ParseInValidStringToTransactionType_ThrowsExceptions(string value)
    {
        var ex = Assert.Throws<ArgumentException>(() => Enum.Parse(typeof(TransactionType), value));
        Assert.NotNull(ex);
    }
}