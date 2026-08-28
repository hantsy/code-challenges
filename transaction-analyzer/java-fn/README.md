# Java version

A functional variant of the transaction analyzer. The analysis pipeline is composed from `java.util.function` APIs (plus a small custom `CheckedFunction` for checked I/O) and executed through a `CompletableFuture` chain.

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
