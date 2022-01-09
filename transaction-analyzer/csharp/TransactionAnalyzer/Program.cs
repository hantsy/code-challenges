using System;
using System.Globalization;
using System.Linq;
using TransactionAnalyzer.Lib;

namespace TransactionAnalyzer
{
    internal class Program
    {
        private static void Main(string[] args)
        {
            Console.WriteLine("Hello World!");
            Console.WriteLine("Current time is: " + DateTime.Now);
            Console.WriteLine("fromDate (dd/MM/yyyy HH:mm:ss):");
            var fromDate = Console.ReadLine();
            Console.WriteLine("toDate (dd/MM/yyyy HH:mm:ss):");
            var toDate = Console.ReadLine();
            Console.WriteLine("merchant:");
            var merchant = Console.ReadLine();

            var fromDateTime = DateTime.ParseExact(fromDate!, "dd/MM/yyyy HH:mm:ss", CultureInfo.InvariantCulture,
                DateTimeStyles.None);
            var toDateTime = DateTime.ParseExact(toDate!, "dd/MM/yyyy HH:mm:ss", CultureInfo.InvariantCulture,
                DateTimeStyles.None);
            var filtered = new InMemoryTransactionRepository(new FileTransactionLoader("./input.csv"))
                .QueryByMerchantAndDateRange(
                    merchant,
                    fromDateTime,
                    toDateTime
                );
            if (filtered.Length == 0)
            {
                Console.WriteLine("No Transactions found.");
            }
            else
            {
                var sum = filtered.Sum(s => s.Amount);
                var avg = filtered.Average(s => s.Amount);

                Console.WriteLine($"Number of transactions = {filtered.Length}");
                Console.WriteLine($"Total Transaction Value = {sum:0.0#}");
                Console.WriteLine($"Average Transaction Value = {avg:0.0#}");
            }
        }
    }
}