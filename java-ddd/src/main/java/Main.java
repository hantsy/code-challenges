import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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

        // loading file
        var input = Main.class.getResourceAsStream("input.csv");

        // declare a TransactionLoader.
        var store = new TransactionStore(input);

        // loading data from input stream
        var loadedTransactions = store.load();

        // persisting the loaded data.
        // it could be a different handler in the real world application, eg.
        // TransactionPersister persister = new ...()
        // persister.persist()
        store.persist(loadedTransactions);

        // declare a TransactionStatisticsReportHandler and assemble the dependencies
        // The `store` could be the `TransactionRepository` for queries in a real world application.
        TransactionStatisticsReportHandler service = new TransactionStatisticsService(store);

        // now try to instantiate a client
        // set the report handler instance
        var client = new TransactionStatisticsReportRequestor(service);

        // gather the input data and send a request and get the report result now.
        var report = client.sendRequest(
                merchant,
                LocalDateTime.parse(fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                LocalDateTime.parse(toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
        );

        //print it to screen.
        System.out.println(report);
    }
}

class TransactionStatisticsReportRequestor {

    private final TransactionStatisticsReportHandler reporter;

    public TransactionStatisticsReportRequestor(TransactionStatisticsReportHandler report) {
        this.reporter = report;
    }

    public String sendRequest(String merchantName, LocalDateTime fromDate, LocalDateTime toDate) {
        return this.reporter.handleReportRequest(new TransactionStatisticsRequest(fromDate, toDate, merchantName))
                .toString();
    }

}

record TransactionStatisticsRequest(
        LocalDateTime fromDate,
        LocalDateTime toDate,
        String merchantName
) {
    public TransactionStatisticsRequest {
        Objects.requireNonNull(merchantName, "merchant name can not be null");
        Objects.requireNonNull(fromDate, "fromDate can not be null");
        Objects.requireNonNull(toDate, "toDate can not be null");
        if (toDate.isBefore(fromDate)) {
            throw new IllegalArgumentException("fromDate should before toDate");
        }
    }
}

sealed class TransactionStatisticsResponse
        permits TransactionStatisticsResponse.Found, TransactionStatisticsResponse.NotFound {

    static final class Found extends TransactionStatisticsResponse {
        private final int count;
        private final BigDecimal totalAmount;
        private final BigDecimal averageAmount;

        public Found(int count, BigDecimal totalAmount, BigDecimal averageAmount) {
            this.count = count;
            this.totalAmount = totalAmount;
            this.averageAmount = averageAmount;
        }

        @Override
        public String toString() {
            var templatedString = """
                    Number of transactions = %d
                    Total Transaction Value = %.2f
                    Average Transaction Value = %.2f
                    """;
            return templatedString.formatted(count, totalAmount, averageAmount);
        }
    }

    static final class NotFound extends TransactionStatisticsResponse {
        @Override
        public String toString() {
            return "No transactions found.";
        }
    }
}

interface TransactionStatisticsReportHandler {
    TransactionStatisticsResponse handleReportRequest(TransactionStatisticsRequest request);
}


class TransactionStatisticsService implements TransactionStatisticsReportHandler {

    //in a real world application, it could be injected by different interfaces.
    private final TransactionRepository store;

    TransactionStatisticsService(TransactionRepository store) {
        this.store = store;
    }

    public TransactionStatisticsResponse handleReportRequest(TransactionStatisticsRequest request) {
        var reversalRelatedTransactionIds = this.store.findByType(TransactionType.REVERSAL)
                .stream().map(Transaction::relatedTransactionId).collect(Collectors.toList());

        var transactions = this.store.findByMerchantAndDateRangeAndType(
                request.merchantName(),
                request.fromDate(),
                request.toDate(),
                TransactionType.PAYMENT
        );

        var filtered = transactions.stream()
                .filter(t -> !reversalRelatedTransactionIds.contains(t.id()))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            return new TransactionStatisticsResponse.NotFound();
        } else {
            var count = filtered.size();
            var sum = filtered.stream()
                    .map(Transaction::amount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            var avg = sum.divide(new BigDecimal(filtered.size()));
            return new TransactionStatisticsResponse.Found(count, sum, avg);
        }

    }
}

class TransactionStore implements TransactionLoader, TransactionPersister, TransactionRepository {
    private final InputStream source;
    private List<Transaction> data;

    public TransactionStore(InputStream source) {
        this.source = source;
    }

    public List<Transaction> load() {
        try (var reader = new BufferedReader(new InputStreamReader(this.source))) {
            return reader.lines().skip(1).map(this::buildTransaction).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
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

    @Override
    public void persist(List<Transaction> data) {
        // in a real world application, it maybe call the database operations or invoke remote requests.
        this.data = data;
    }

    @Override
    public List<Transaction> findByType(TransactionType type) {
        return data.stream()
                .filter(it -> it.type() == type)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByMerchantAndDateRangeAndType(String merchant, LocalDateTime fromDate, LocalDateTime toDate, TransactionType type) {
        return data.stream()
                .filter(it -> it.merchantName().equals(merchant)
                        && it.transactedAt().isAfter(fromDate)
                        && it.transactedAt().isBefore(toDate)
                        && it.type() == type
                )
                .collect(Collectors.toList());
    }
}


interface TransactionLoader {
    List<Transaction> load();
}

interface TransactionPersister {
    void persist(List<Transaction> data);
}

interface TransactionRepository {
    List<Transaction> findByType(TransactionType type);

    List<Transaction> findByMerchantAndDateRangeAndType(String merchant, LocalDateTime fromDate, LocalDateTime toDate, TransactionType type);
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

