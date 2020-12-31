import { LocalDateTime } from "@js-joda/core";
import { TransactionType } from "./transaction-type.enum";
import { Transaction } from "./transaction.interface";
import Big from "big.js";


describe('Transaction', () => {

    let instance: Transaction;

    beforeEach(() => {
        instance = {
            id: "test",
            transactedAt: LocalDateTime.now(),
            amount: Big("5.99"),
            merchantName: "testMerchant",
            type: TransactionType["PAYMENT"],
            relatedTransactionId: ""
        };
        console.log("instance:" + JSON.stringify(instance));
    });

    it('verify the Transaction instance ', () => {
        expect(instance.id).toBe("test");
        expect(instance.transactedAt.isBefore(LocalDateTime.now())).toBeTruthy();
        expect(instance.merchantName).toBe("testMerchant");
        expect(instance.amount.toNumber()).toBeCloseTo(5.99, 2);
        expect(instance.type).toEqual(TransactionType.PAYMENT);
        expect(instance.relatedTransactionId).toBe("");
    });
});
