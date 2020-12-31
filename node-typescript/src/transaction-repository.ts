import { LocalDateTime } from "@js-joda/core";
import { TransactionType } from "./transaction-type.enum";
import { Transaction } from "./transaction.interface";
import { TransactionLoader } from "./transaction-loader";

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

        const reversalRelatedIds = this.data.filter(it => it.type == TransactionType.REVERSAL)
            .map(t => t.relatedTransactionId);
        console.log("reversal reatled ids:" + JSON.stringify(reversalRelatedIds));

        return this.data.filter(it => it.merchantName == merchant
            && it.transactedAt.isAfter(fromDate)
            && it.transactedAt.isBefore(toDate)
            && it.type == TransactionType.PAYMENT
            && !reversalRelatedIds.includes(it.id)
        );
    }
}
