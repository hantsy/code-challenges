import { LocalDateTime } from "@js-joda/core";
import { TransactionType } from "./TransactionType";
import { Transaction } from "./Transaction";
import { TransactionLoader } from "./TransactionLoader";

export class TransactionRepository {
    private data: Transaction[];

    constructor(private loader: TransactionLoader) {
        this.data = this.loader.load();
        console.log('loaded transactions:' + JSON.stringify(this.data));
    }

    queryByMerchantAndDateRange(
        merchant: string,
        fromDate: LocalDateTime,
        toDate: LocalDateTime
    ): Transaction[] {
        console.log("Merchant name:" + merchant);
        console.log("From Date:" + fromDate);
        console.log("To Date:" + toDate);

        const reversal = this.data.filter(it => it.type == TransactionType.REVERSAL)
            .map(t => t.relatedTransactionId);
        console.log("reversal reatled ids:" + JSON.stringify(reversal));

        return this.data.filter(it => it.merchantName == merchant
            && it.transactedAt.isAfter(fromDate)
            && it.transactedAt.isBefore(toDate)
            && it.type == TransactionType.PAYMENT
            && reversal.findIndex(r => r == it.id) < 0
        );
    }
}
