import Big from 'big.js';
import { DateTimeFormatter, LocalDateTime } from '@js-joda/core';
import { TransactionType } from './transaction-type.enum';
import { Transaction } from './transaction.interface';
import { TransactionLoader } from './transaction-loader.interface';
import { readFileSync } from 'node:fs';
export class DefaultTransactionLoader implements TransactionLoader {
  constructor(private file: string) {}

  load(): Transaction[] {
    const lines = readFileSync(this.file, 'utf-8').split('\n').filter(Boolean);
    return lines.slice(1).map((line: string) => this.buildTransaction(line));
  }

  buildTransaction(line: string): Transaction {
    console.log('reading line:' + line);
    const fields = line.split(',');
    console.log('fields: ' + JSON.stringify(fields));
    return {
      id: fields[0].trim(),
      transactedAt: LocalDateTime.parse(
        fields[1].trim(),
        DateTimeFormatter.ofPattern('dd/MM/yyyy HH:mm:ss')
      ),
      amount: Big(fields[2].trim()),
      merchantName: fields[3].trim(),
      type: TransactionType[fields[4].trim()],
      relatedTransactionId: fields[5].trim()
    };
  }
}
