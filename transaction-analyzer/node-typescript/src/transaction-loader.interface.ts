import { Transaction } from "./transaction.interface";

export interface TransactionLoader {
    load(): Transaction[];
}
