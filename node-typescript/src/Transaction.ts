import Big from "big.js";
import { LocalDateTime } from "@js-joda/core";
import { TransactionType } from "./TransactionType";

//AKNBVHMN, 20/08/2020 13:14:11, 10.95, Kwik-E-Mart, REVERSAL, YGXKOEIA
export interface Transaction {
    readonly id: string;
    readonly transactedAt: LocalDateTime;
    readonly amout: Big;
    readonly merchantName: string;
    readonly type: TransactionType;
    readonly relatedTransactionId?: string;
}
