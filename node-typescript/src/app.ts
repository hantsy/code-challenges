import Big from "big.js";
import { DateTimeFormatter, LocalDateTime } from "@js-joda/core";
import { DefaultTransactionLoader } from "./transaction-loader";
import { TransactionRepository } from "./transaction-repository";

export class App {
    public static run(): void {

        const readlineSync = require('readline-sync');
        const fromDate = readlineSync.question('fromDate (dd/MM/yyyy HH:mm:ss):');
        const toDate = readlineSync.question('toDate (dd/MM/yyyy HH:mm:ss):');
        const merchantName = readlineSync.question('merchant:');

        const filterd = new TransactionRepository(new DefaultTransactionLoader('./input.csv'))
            .queryByMerchantAndDateRange(
                merchantName,
                LocalDateTime.parse(fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                LocalDateTime.parse(toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
            );
        console.log("filtered transactions:" + JSON.stringify(filterd));

        if (filterd.length == 0) {
            console.log("No transactions found.");
        } else {
            const sum = filterd.reduce((a, c) => a.plus(c.amount), Big(0));
            const avg = sum.div(Big(filterd.length));
            console.log('Number of transcations = ' + filterd.length);
            console.log('Total Transaction Value = ' + sum.toFixed(2));
            console.log('Average Transaction Value = ' + avg.toFixed(2));
        }

    }
}
