namespace TransactionAnalyzer.Lib;

public interface ITransactionLoader
{
    /// <summary>
    ///     Load transactions from an external resource.
    /// </summary>
    /// <returns>The loaded transactions.</returns>
    Transaction[] Load();
}