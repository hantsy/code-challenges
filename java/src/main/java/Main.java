import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
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
        var result = new TransactionRepository(new InputStreamTransactionLoader(input))
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
                    Total Transaction Value = %.2f
                    Average Transaction Value = %.2f
                    """;
            var sum = result.stream()
                    .map(Transaction::amount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            var avg = sum.divide(new BigDecimal(result.size()), RoundingMode.HALF_UP);
            System.out.printf((printTemplate) + "%n", result.size(), sum, avg);
        }
    }
}

class TransactionRepository {

    private final TransactionLoader loader;

    TransactionRepository(TransactionLoader _loader) {
        loader = _loader;
    }

    public List<Transaction> queryByMerchantAndDateRange(
            String merchant,
            LocalDateTime fromDate,
            LocalDateTime toDate
    ) {
        List<Transaction> data;
        try {
            data = this.loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            data = Collections.emptyList();
        }
        var reversal = data.stream()
                .filter(it -> it.type() == TransactionType.REVERSAL)
                .map(Transaction::relatedTransactionId)
                .collect(Collectors.toList());
        return data.stream()
                .filter(it -> it.merchantName().equals(merchant)
                        && it.transactedAt().isAfter(fromDate)
                        && it.transactedAt().isBefore(toDate)
                        && it.type() == TransactionType.PAYMENT
                        && !reversal.contains(it.id())
                )
                .collect(Collectors.toList());
    }

}

interface TransactionLoader {
    List<Transaction> load() throws IOException;
}

/*
class FileTransactionLoader implements TransactionLoader{

    private final File file;

    public FileTransactionLoader(File file) {
        this.file = file;
    }

    @Override
    public List<Transaction> load() throws IOException {
        return null;
    }
}*/

class InputStreamTransactionLoader implements TransactionLoader {
    final InputStream source;

    public InputStreamTransactionLoader(InputStream source) {
        this.source = source;
    }

    @Override
    public List<Transaction> load() throws IOException {
        try (var reader = new BufferedReader(new InputStreamReader(this.source))) {
            return reader.lines().skip(1).map(this::buildTransaction).collect(Collectors.toList());
        }
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

