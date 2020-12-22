import Big from "big.js";
import { DateTimeFormatter, LocalDateTime } from "@js-joda/core";
import { TransactionType } from "./TransactionType";
import { Transaction } from "./Transaction";

export class TransactionLoader {
    constructor(private file: string) {
    }

    load(): Transaction[] {
        const lines = require('fs')
            .readFileSync(this.file, 'utf-8')
            .split('\n')
            .filter(Boolean);

        return lines.slice(1).map((line: string) => this.buildTranaction(line));
    }

    buildTranaction(line: string): Transaction {
        console.log('reading line:' + line);
        const fields = line.split(',');
        console.log('fields: ' + JSON.stringify(fields));
        return {
            id: fields[0].trim(),
            transactedAt: LocalDateTime.parse(fields[1].trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
            amout: Big(fields[2].trim()),
            merchantName: fields[3].trim(),
            type: TransactionType[fields[4].trim()],
            relatedTransactionId: fields[5].trim()
        };
    }
}
