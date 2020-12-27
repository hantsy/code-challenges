<?php


namespace TransactionAnalyser\Tests;


use Brick\DateTime\LocalDateTime;
use Brick\Math\BigDecimal;
use DateTime;
use TransactionAnalyser\Transaction;
use TransactionAnalyser\TransactionType;

class Fixtures
{
    static function transactionData(): array
    {
        return [
            new Transaction(
                "WLMFRDGD",
                LocalDateTime::fromDateTime(DateTime::createFromFormat('d/m/Y H:i:s', "20/08/2020 12:45:33")),
                BigDecimal::of(59.99),
                "Kwik-E-Mart",
                new TransactionType("PAYMENT"),
                ""
            ),
            new Transaction(
                "YGXKOEIA",
                LocalDateTime::fromDateTime(DateTime::createFromFormat('d/m/Y H:i:s', "20/08/2020 12:46:17")),
                BigDecimal::of(10.95),
                "Kwik-E-Mart",
                new TransactionType("PAYMENT"),
                ""
            ),
            new Transaction(
                "LFVCTEYM",
                LocalDateTime::fromDateTime(DateTime::createFromFormat('d/m/Y H:i:s', "20/08/2020 12:50:02")),
                BigDecimal::of(5.00),
                "MacLaren",
                new TransactionType("PAYMENT"),
                ""
            ),
            new Transaction(
                "SUOVOISP",
                LocalDateTime::fromDateTime(DateTime::createFromFormat('d/m/Y H:i:s', "20/08/2020 13:12:22")),
                BigDecimal::of(5.00),
                "Kwik-E-Mart",
                new TransactionType("PAYMENT"),
                ""
            ),
            new Transaction(
                "AKNBVHMN",
                LocalDateTime::fromDateTime(DateTime::createFromFormat('d/m/Y H:i:s', "20/08/2020 13:14:11")),
                BigDecimal::of(10.95),
                "Kwik-E-Mart",
                new TransactionType("REVERSAL"),
                "YGXKOEIA"
            ),
            new Transaction(
                "JYAPKZFZ",
                LocalDateTime::fromDateTime(DateTime::createFromFormat('d/m/Y H:i:s', "20/08/2020 14:07:10")),
                BigDecimal::of(99.50),
                "MacLaren",
                new TransactionType("PAYMENT"),
                ""
            ),
        ];
    }
}