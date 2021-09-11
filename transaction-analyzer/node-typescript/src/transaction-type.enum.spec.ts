import { TransactionType } from "./transaction-type.enum";

describe("TransactionType", () => {

    it("should parse 'PAYMENT' as TransactionType.PAYMNET ", () => {
        expect(TransactionType["PAYMENT"]).toEqual(TransactionType.PAYMENT);
    });

    it("should parse 'REVERSAL' as TransactionType.REVERSAL ", () => {
        expect(TransactionType["REVERSAL"]).toEqual(TransactionType.REVERSAL);
    });

    it("should parse 'UNKNOWN' as undefined ", () => {
        expect(TransactionType["UNKNOWN"]).toBeUndefined();
    });
});
