# Java version (DDD / Hexagonal architecture)

A variant of the transaction analyzer structured with a DDD layered package
architecture (ports & adapters / hexagonal architecture).

## Architecture

```
com.example.demo
├── domain
│   ├── model     # Transaction, TransactionType, Notification (no outward dependencies)
│   └── service   # domain services implementing the inbound ports
├── port
│   ├── in        # driving ports (+ report request/response)
│   └── out       # driven ports (loader, persister, repository, notifier)
└── adapter
    ├── in        # driving adapters (console)
    └── out       # driven adapters (csv, in-memory persistence, notification)
```

The dependency rules are enforced by [ArchUnit](https://www.archunit.org/) tests:
the domain model must not depend on any other package, domain services and
ports must not depend on adapters, ports may only depend on the domain model,
and inbound/outbound adapters must not depend on each other.

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
