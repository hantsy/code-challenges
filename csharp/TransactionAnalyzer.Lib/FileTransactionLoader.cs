using System;
using System.Globalization;
using System.IO;
using System.Linq;

namespace TransactionAnalyzer.Lib
{
    public class FileTransactionLoader : ITransactionLoader
    {
        private readonly string _csvFilePath;

        public FileTransactionLoader(string csvFilePath)
        {
            _csvFilePath = csvFilePath;
        }

        public Transaction[] Load()
        {
            var lines = File.ReadAllLines(_csvFilePath);
            return lines.Skip(1).Select(BuildTransaction).ToArray();
        }

        private Transaction BuildTransaction(string line)
        {
            Console.WriteLine("reading line:" + line);
            // Split into array
            var fields = line.Split(",");
            Console.WriteLine("Split into fields:" + fields.Length);

            //try to parse the fields
            Enum.TryParse(fields[4].Trim(), out TransactionType parsedType);
            DateTime.TryParseExact(fields[1].Trim(), "dd/MM/yyyy HH:mm:ss", CultureInfo.InvariantCulture,
                DateTimeStyles.None, out var parsedTransactedAt);
            return new Transaction{
                Id = fields[0].Trim(),
                TransactedAt = parsedTransactedAt,
                Amount = Convert.ToDecimal(fields[2].Trim()),
                MerchantName = fields[3].Trim(),
                Type = parsedType,
                RelatedTransactionId = fields[5]?.Trim()
            };
        }
    }
}