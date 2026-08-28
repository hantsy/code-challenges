# Java version (DDD layered architecture)

A variant of the transaction analyzer structured with a simple DDD layered
package architecture.

## Architecture

```
com.example.demo
├── application
│   ├── LoadTransactionsService                # interface, command service
│   ├── QueryValidPaymentTransactionsService   # interface, query service
│   ├── GenerateTransactionStatisticsReportService
│   ├── TransactionStatisticsRequest/Response  # query DTOs
│   └── internal                               # service implementations
│       ├── DefaultLoadTransactionsService
│       ├── DefaultQueryValidPaymentTransactionsService
│       └── DefaultGenerateTransactionStatisticsReportService
├── domain
│   ├── model                    # Transaction, TransactionType, Notification (pure)
│   ├── repository               # TransactionRepository interface
│   └── service                  # domain service interfaces
│       ├── TransactionLoader    # source of transactions
│       └── NotificationSender   # outbound notification
├── infrastructure
│   ├── csv                      # CsvTransactionLoader
│   ├── notification             # EmailNotificationSender, SlackNotificationSender
│   └── persistence              # InMemoryTransactionRepository
└── interfaces
    └── console                  # TransactionReportConsole
```

The dependency rules are enforced by [ArchUnit](https://www.archunit.org/) tests:
the domain layer must not depend on any other layer, the application layer must
not depend on infrastructure or interfaces, the interfaces layer may only
depend on the application layer, and the infrastructure layer must not depend
on interfaces or application service implementations (`application.internal`).
Application services expose interfaces in `application`; their implementations
reside in `application.internal`, and domain service interfaces (`TransactionLoader`,
`NotificationSender`) are declared in `domain.service`.

## Prerequisite

* Java 25
* Apache Maven 3.9+

## Build

Build the project and run the application with the following command.

```bash
mvn clean package exec:java
```

## Test

```bash
mvn test
```
