# Transaction Analyzer

The *Transaction Analyzer* application is use for loading transaction records from an existing CSV files and generating statistics report after analyzing the loaded data.

# Requirements

Given a CSV file contains some transaction records from a financial system, design an application to analyze the data in the CSV file and generate a statistics report.

When analyzing the records, the `REVERSAL` transaction and it related transaction should be excluded.

Assume there is a CSV example file contains the following transactions.

```csv 
ID, Date, Amount, Merchant, Type, Related Transaction
WLMFRDGD, 20/08/2020 12:45:33, 59.99, Kwik-E-Mart, PAYMENT,
YGXKOEIA, 20/08/2020 12:46:17, 10.95, Kwik-E-Mart, PAYMENT,
LFVCTEYM, 20/08/2020 12:50:02, 5.00, MacLaren, PAYMENT,
SUOVOISP, 20/08/2020 13:12:22, 5.00, Kwik-E-Mart, PAYMENT,
AKNBVHMN, 20/08/2020 13:14:11, 10.95, Kwik-E-Mart, REVERSAL, YGXKOEIA
JYAPKZFZ, 20/08/2020 14:07:10, 99.50, MacLaren, PAYMENT,
```

When user input the following arguments as query criteria.

```bash
fromDate: 20/08/2020 12:00:00
toDate: 20/08/2020 13:00:00
merchant: Kwik-E-Mart
```

It should output the result similar to this.

```bash
Number of transactions = 1
Average Transaction Value = 59.99
```

If no transaction record found, print the following message instead.

```bash
No transactions found.
```

## My implemenation Examples

To make things simpler, I tried to avoid to use any third party library to parse the CSV file, and also didn't adopt a database to store the parsed data from CSV, all the analyzing work is done in memory.

As as a toy for myself, I've created a collection of implementations written in different languages and techniques.

| Example | Description|
|---|---|
|[java](./transaction-analyzer/java)| Java examples|
|[java-ddd](./transaction-analyzer/java-ddd) (*Working in progress*)|Java with DDD/Onion/Hexagon Architecture|
|[java-fn](./transaction-analyzer/java-fn)|Java Functional Programming(Function, Supplier, Consumer, CompletableFuture)|
|[kotlin](./transaction-analyzer/kotlin) | Kotlin |
|[scala](./transaction-analyzer/scala) | Scala | 
|[php](./transaction-analyzer/php) | PHP |
|[csharp](./transaction-analyzer/csharp) | C# |
|[node-typescript](./transaction-analyzer/node-typescript/)|Node(Typescript)| 
|[go](./transaction-analyzer/go) | Go |

## More ...

Desire to dive deeper in more Kotlin examples, check the following projects which used Kotlin with the cool Spring support, including Kotlin Coroutines and Kotlin DSL.

* https://github.com/hantsy/spring-kotlin-coroutines-sample
* https://github.com/hantsy/spring-kotlin-dsl-sample 

Want to explore an example close to real world applications with testing codes and CI/CD integrations. Check the following example which is configured with most the popluar CI service, including Github Actions, Travis, Circle, Codefresh, Drone, SemaphoreCI, Appveyor, Shippable, etc.

* https://github.com/hantsy/spring-reactive-jwt-sample

Feel tired with frameworks and return back to Java EE/Jakarta EE, it is also great. Check the following template projects to start your Jakarta EE application development freely. These projects include sample test codes and configurations with Arquillian against the popluar application servers, such as WildFly, Payara/Glassfish, Open Liberty, Apache TomEE, etc.

* https://github.com/hantsy/jakartaee8-starter
* https://github.com/hantsy/jakartaee9-starter-boilerplate

Enjoy!
