import { mock } from 'jest-mock-extended';
import { DefaultTransactionLoader } from "./transaction-loader";
import { Transaction } from "./transaction.interface";
import { TransactionLoader } from "./transaction-loader";
import { DateTimeFormatter, LocalDateTime } from '@js-joda/core';
import { TransactionType } from './transaction-type.enum';
import Big from 'big.js';
import { TransactionRepository } from './transaction-repository';
import { mocked } from 'ts-jest/utils';
import { Mock, It, Times, IMock } from "moq.ts";
import { anyNumber, anyString, instance, mock as mockitoMock, reset, verify, when } from 'ts-mockito';


//     ID, Date, Amount, Merchant, Type, Related Transaction
// WLMFRDGD, 20/08/2020 12:45:33, 59.99, Kwik-E-Mart, PAYMENT,
// YGXKOEIA, 20/08/2020 12:46:17, 10.95, Kwik-E-Mart, PAYMENT,
// LFVCTEYM, 20/08/2020 12:50:02, 5.00, MacLaren, PAYMENT,
// SUOVOISP, 20/08/2020 13:12:22, 5.00, Kwik-E-Mart, PAYMENT,
// AKNBVHMN, 20/08/2020 13:14:11, 10.95, Kwik-E-Mart, REVERSAL, YGXKOEIA
// JYAPKZFZ, 20/08/2020 14:07:10, 99.50, MacLaren, PAYMENT,
const fakeTransactionData = [
    {
        id: "WLMFRDGD",
        transactedAt: LocalDateTime.parse("20/08/2020 12:45:33", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
        amount: Big(59.99),
        merchantName: "Kwik-E-Mart",
        type: TransactionType["PAYMENT"],
        relatedTransactionId: ""
    } as Transaction,
    {
        id: "YGXKOEIA",
        transactedAt: LocalDateTime.parse("20/08/2020 12:46:17", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
        amount: Big(10.95),
        merchantName: "Kwik-E-Mart",
        type: TransactionType["PAYMENT"],
        relatedTransactionId: ""
    } as Transaction,
    {
        id: "LFVCTEYM",
        transactedAt: LocalDateTime.parse("20/08/2020 12:50:02", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
        amount: Big(5.00),
        merchantName: "MacLaren",
        type: TransactionType["PAYMENT"],
        relatedTransactionId: ""
    } as Transaction,
    {
        id: "SUOVOISP",
        transactedAt: LocalDateTime.parse("20/08/2020 13:12:22", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
        amount: Big(5.00),
        merchantName: "Kwik-E-Mart",
        type: TransactionType["PAYMENT"],
        relatedTransactionId: ""
    } as Transaction,
    {
        id: "AKNBVHMN",
        transactedAt: LocalDateTime.parse("20/08/2020 13:14:11", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
        amount: Big(10.95),
        merchantName: "Kwik-E-Mart",
        type: TransactionType["REVERSAL"],
        relatedTransactionId: "YGXKOEIA"
    } as Transaction,
    {
        id: "JYAPKZFZ",
        transactedAt: LocalDateTime.parse("20/08/2020 14:07:10", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
        amount: Big(99.50),
        merchantName: "MacLaren",
        type: TransactionType["PAYMENT"],
        relatedTransactionId: ""
    } as Transaction
] as Transaction[];

describe("TransactionRepository", () => {

    describe("mocking loader using jest-mock-extended", () => {
        it(("test query by merchant and date range(jest-mock-extended)"), () => {
            let loader = mock<TransactionLoader>();
            loader.load.mockImplementation(() => {
                return fakeTransactionData;
            });

            const merchantName = "Kwik-E-Mart";
            const fromDate = "20/08/2020 12:00:00";
            const toDate = "20/08/2020 13:00:00";
            const transactions = new TransactionRepository(loader)
                .queryByMerchantAndDateRange(
                    merchantName,
                    LocalDateTime.parse(fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                    LocalDateTime.parse(toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                )
            expect(transactions.length).toBe(1);
            expect(loader.load).toBeCalledTimes(1);
        });
    });

    describe(("mocking loader using Moq.ts"), () => {
        let mockedloader: IMock<TransactionLoader>;
        beforeEach(() => {
            mockedloader = new Mock<TransactionLoader>()
                .setup(instance => instance.load())
                .returns(fakeTransactionData);
        })

        afterEach(() => {

        })

        it("test query by merchant and date range(moq.ts)", () => {
            const repsoitory = new TransactionRepository(mockedloader.object());
            const merchantName = "Kwik-E-Mart";
            const fromDate = "20/08/2020 12:00:00";
            const toDate = "20/08/2020 13:00:00";
            const transactions = repsoitory
                .queryByMerchantAndDateRange(
                    merchantName,
                    LocalDateTime.parse(fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                    LocalDateTime.parse(toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                )
            expect(transactions.length).toBe(1);
            mockedloader.verify(instance => instance.load(), Times.Exactly(1));
        });

    });

    describe(("mocking loader using ts-mockito"), () => {
        let mockedloader: TransactionLoader;
        beforeEach(() => {
            mockedloader = mockitoMock<TransactionLoader>();
            when(mockedloader.load()).thenReturn(fakeTransactionData);
        })

        afterEach(() => {
            reset(mockedloader);
        })

        it("test query by merchant and date range(ts-mockito)", () => {

            const repsoitory = new TransactionRepository(instance(mockedloader));
            const merchantName = "Kwik-E-Mart";
            const fromDate = "20/08/2020 12:00:00";
            const toDate = "20/08/2020 13:00:00";
            const transactions = repsoitory
                .queryByMerchantAndDateRange(
                    merchantName,
                    LocalDateTime.parse(fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                    LocalDateTime.parse(toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                )
            expect(transactions.length).toBe(1);
            verify(mockedloader.load()).once();
        });

    });
});
