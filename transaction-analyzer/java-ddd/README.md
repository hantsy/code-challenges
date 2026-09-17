# Java version (DDD layered architecture)

A variant of the transaction analyzer structured with a simple DDD layered
package architecture.

## Architecture

```
com.example.demo
├── application
│   ├── LoadTransactionsService                # interface, command service
│   ├── GenerateTransactionStatisticsReportService # interface, query service
│   ├── TransactionStatisticsRequest/Response  # query DTOs
│   └── internal                               # service implementations
│       ├── DefaultLoadTransactionsService
│       └── DefaultGenerateTransactionStatisticsReportService # query + statistics + notification
├── domain
│   ├── model                    # Transaction, TransactionType, Notification (pure)
│   ├── repository               # TransactionRepository interface (findValidPayments)
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

Excluding reversed payments is part of the query itself, so it is expressed as a
single domain-intent method, `TransactionRepository#findValidPayments`, and implemented
by each repository. `DefaultGenerateTransactionStatisticsReportService` owns the
remaining application flow: run the query, notify all `NotificationSender`s, then
reduce the result into `TransactionStatisticsResponse`.

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
