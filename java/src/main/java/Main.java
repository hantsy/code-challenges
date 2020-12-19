import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        System.out.println("fromDate (dd/MM/yyyy HH:mm:ss):");
        var fromDate = scanner.nextLine();
        System.out.println("toDate (dd/MM/yyyy HH:mm:ss):");
        var toDate = scanner.nextLine();
        System.out.println("merchant:");
        var merchant = scanner.nextLine();

        //load file
        var input = Main.class.getResourceAsStream("input.csv");

        //parse and query
        var result = new TransactionRepository(new TransactionLoader(input))
                .queryByMerchantAndDateRange(
                        merchant,
                        LocalDateTime.parse(fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                        LocalDateTime.parse(toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                );

        //print result
        if (result.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            String printTemplate = """
                    Number of transactions = %d
                    Average Transaction Value = %.2f
                    """;
            var sum = result.stream()
                    .map(it -> it.amount())
                    .reduce(BigDecimal.ZERO, (item, transaction) -> item.add(transaction));
            var avg = sum.divide(new BigDecimal(result.size()));
            System.out.println(printTemplate.formatted(result.size(), avg));
        }
    }
}

class TransactionRepository {

    private List<Transaction> data;

    TransactionRepository(TransactionLoader _loader) {
        this.data = _loader.load();
    }

    public List<Transaction> queryByMerchantAndDateRange(
            String merchant,
            LocalDateTime fromDate,
            LocalDateTime toDate
    ) {
        var reversal = data.stream()
                .filter(it -> it.type() == TransactionType.REVERSAL)
                .map(Transaction::relatedTransactionId)
                .collect(Collectors.toList());
        return data.stream()
                .filter(it -> it.merchantName().equals(merchant)
                        && it.transactedAt().isAfter(fromDate)
                        && it.transactedAt().isBefore(toDate)
                        && it.type() != TransactionType.REVERSAL
                        && !reversal.contains(it.id())
                )
                .collect(Collectors.toList());
    }

}

class TransactionLoader {
    final InputStream source;

    public TransactionLoader(InputStream source) {
        this.source = source;
    }

    public List<Transaction> load() {
        var reader = new BufferedReader(new InputStreamReader(this.source));
        return reader.lines().skip(1).map(this::buildTransaction).collect(Collectors.toList());
    }

    private Transaction buildTransaction(String line) {
        System.out.println("reading line:" + line);
        var fields = line.split(",");
        System.out.println("fields: " + fields.length);
        return new Transaction(
                fields[0].trim(),
                LocalDateTime.parse(fields[1].trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                new BigDecimal(fields[2].trim()),
                fields[3].trim(),
                TransactionType.valueOf(fields[4].trim()),
                fields.length == 6 ? fields[5].trim() : null
        );
    }
}

//AKNBVHMN, 20/08/2020 13:14:11, 10.95, Kwik-E-Mart, REVERSAL, YGXKOEIA
record Transaction(
        String id,
        LocalDateTime transactedAt,
        BigDecimal amount,
        String merchantName,
        TransactionType type,
        String relatedTransactionId
) {
}

enum TransactionType {
    PAYMENT,
    REVERSAL,
}

