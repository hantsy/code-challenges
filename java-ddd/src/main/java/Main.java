import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        System.out.println("fromDate (dd/MM/yyyy HH:mm:ss):");
        var fromDate = scanner.nextLine();
        System.out.println("toDate (dd/MM/yyyy HH:mm:ss):");
        var toDate = scanner.nextLine();
        System.out.println("merchant:");
        var merchant = scanner.nextLine();

        //registering notifiers.
        NotifiterRegistry.register(new SlackNotifier(), new EmailNotifier(), notification -> LOGGER.info("Sent from a dummy notifier"));

        // loading file
        var input = Main.class.getResourceAsStream("input.csv");

        // declare a TransactionLoader.
        var loader = new TransactionLoaderAdapter(input);

        // declare a TransactionPersister.
        // it is also a `TransactionRepository`
        var store = new TransactionStoreAdapter();

        // declare a TransactionLoadService to load transactions from CSV files
        // and persist them into database.
        var loadService = new TransactionLoadService(loader, store);
        loadService.loadAndPersist();

        // initializing a `TransactionQueryService`.
        // The `store` is a `TransactionRepository` for queries.
        var queryService = new TransactionQueryService(store);

        // declare a TransactionStatisticsReportHandler and assemble the dependencies
        var handler = new TransactionStatisticsReportHandlerAdapter(queryService);

        // now try to instantiate a client
        // set the report handler instance
        var client = new TransactionStatisticsReportRequestor(handler);

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

class TransactionStatisticsReportHandlerAdapter implements TransactionStatisticsReportHandler {

    //in a real world application, it could be an interfaces.
    private final GetAllValidPaymentTransactionsUseCase service;

    TransactionStatisticsReportHandlerAdapter(GetAllValidPaymentTransactionsUseCase service) {
        this.service = service;
    }

    public TransactionStatisticsResponse handleReportRequest(TransactionStatisticsRequest request) {

        var filtered = this.service.queryValidPaymentTransactions(
                request.merchantName(),
                request.fromDate(),
                request.toDate()
        );

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

//the use case of return all valid transactions.
interface GetAllValidPaymentTransactionsUseCase {
    List<Transaction> queryValidPaymentTransactions(String merchant, LocalDateTime fromDate, LocalDateTime toDate);
}

// Implementing GetAllValidPaymentTransactionsUseCase.
//
// Return all transactions according to the given merchant, fromDate, toDate, and filter transactions:
// 1. all `REVERSAL` transactions should be excluded.
// 2. if the transaction type is `PAYMENT`, but there is an existing `REVERSAL` transaction related to it,
// it also should be excluded.
class TransactionQueryService implements GetAllValidPaymentTransactionsUseCase {
    private static final Logger LOGGER = Logger.getLogger(TransactionQueryService.class.getName());

    private final TransactionRepository store;

    TransactionQueryService(TransactionRepository store) {
        this.store = store;
    }

    public List<Transaction> queryValidPaymentTransactions(String merchant, LocalDateTime fromDate, LocalDateTime toDate) {
        var reversalRelatedTransactionIds = this.store.findByType(TransactionType.REVERSAL)
                .stream().map(Transaction::relatedTransactionId).collect(Collectors.toList());

        var transactions = this.store.findByMerchantAndDateRangeAndType(
                merchant,
                fromDate,
                toDate,
                TransactionType.PAYMENT
        );

        var filtered = transactions.stream()
                .filter(t -> !reversalRelatedTransactionIds.contains(t.id()))
                .collect(Collectors.toList());
        LOGGER.log(Level.INFO, "{0} transactions found.", filtered.size());

        // send notifications.
        // a better solution: publisher/subscriber pattern to decouple Notifiter API from service.
        NotifiterRegistry.availableNotifiers().forEach(
                notifier -> notifier.notify(new Notification("queryValidPaymentTransactions is executed.", LocalDateTime.now()))
        );
        return filtered;
    }
}

class NotifiterRegistry {
    private static final List<Notifier> notifiers = new ArrayList<>();

    public static void register(Notifier... notifier) {
        notifiers.addAll(Arrays.asList(notifier));
    }

    public static List<Notifier> availableNotifiers() {
        return notifiers;
    }
}

//adapter to email service.
class EmailNotifier implements Notifier {
    private static final Logger LOGGER = Logger.getLogger(EmailNotifier.class.getName());

    @Override
    public void notify(Notification notification) {
        LOGGER.log(Level.INFO, "send notification by email: {0}", new Object[]{notification});
        //...
    }
}

//adapter to slack notifier
class SlackNotifier implements Notifier {
    private static final Logger LOGGER = Logger.getLogger(SlackNotifier.class.getName());

    @Override
    public void notify(Notification notification) {
        LOGGER.log(Level.INFO, "send notification to slack channel: {0}", new Object[]{notification});
        //...
    }
}

interface Notifier {
    void notify(Notification notification);
}

record Notification(
        String message,
        LocalDateTime sentAt
) {
}

// the use case of Loading transactions.
interface LoadTransactionRecordsFromCsvUseCase {
    void loadAndPersist();
}

// Implementing LoadTransactionRecordsFromCsvUseCase.
//
//in a real world application, it is could be a mature ELT solution, such as Spring Batch, or Spring Cloud DataFlow.
class TransactionLoadService implements LoadTransactionRecordsFromCsvUseCase {
    private static final Logger LOGGER = Logger.getLogger(TransactionLoadService.class.getName());

    private final TransactionLoader loader;
    private final TransactionPersister persister;

    public TransactionLoadService(TransactionLoader loader, TransactionPersister persister) {
        this.loader = loader;
        this.persister = persister;
    }

    public void loadAndPersist() {
        var transactions = this.loader.load();
        LOGGER.log(Level.INFO, "{0} transactions loaded from csv files", transactions.size());
        this.persister.persist(transactions);
    }
}

class InMemoryDb {
    static List<Transaction> data = Collections.emptyList();
}

class TransactionLoaderAdapter implements TransactionLoader {

    private final InputStream source;

    public TransactionLoaderAdapter(InputStream source) {
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
}

class TransactionStoreAdapter implements TransactionPersister, TransactionRepository {

    public TransactionStoreAdapter() {
    }

    @Override
    public void persist(List<Transaction> data) {
        // in a real world application, it maybe call the database operations or invoke remote requests.
        InMemoryDb.data = data;
    }

    @Override
    public List<Transaction> findByType(TransactionType type) {
        return InMemoryDb.data.stream()
                .filter(it -> it.type() == type)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByMerchantAndDateRangeAndType(String merchant, LocalDateTime fromDate, LocalDateTime toDate, TransactionType type) {
        return InMemoryDb.data.stream()
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

