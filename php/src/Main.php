<?php

namespace Hantsy\TransactionAnalyser;

use Brick\DateTime\LocalDateTime;
use Brick\Math\BigDecimal;
use DateTime;

class Main
{

    static function run()
    {
        $fromDate = readline("fromDate (dd/MM/yyyy HH:mm:ss):");
        $toDate = readline("toDate (dd/MM/yyyy HH:mm:ss):");
        $merchant = readline("merchant:");

        // locate file
        $file = __DIR__ . "/input.csv";

        // loading data.
        $loader = new TransactionLoader($file);
        $transactions = $loader->load();

        //var_dump($transactions);
        // generating report.
        $reversalTransactions = array_filter($transactions,
            function ($v, $k) {
                return $v->getType() == TransactionType::REVERSAL;
            },
            ARRAY_FILTER_USE_BOTH
        );

        echo "\$reversalTransactions:" . PHP_EOL;
        var_dump($reversalTransactions);

        $reversalRelatedTransactionIds = array_map(
            function ($v) {
                return $v->getRelatedTransactionId();
            },
            $reversalTransactions
        );

        echo "\$reversalRelatedTransactionIds:" . PHP_EOL;
        var_dump($reversalRelatedTransactionIds);

        $filteredTransactions = array_filter($transactions,
            function ($v, $k) use ($reversalRelatedTransactionIds, $toDate, $fromDate, $merchant) {
                return $v->getMerchantName() == $merchant
                    && $v->getTransactedAt()->isAfter(LocalDateTime::fromDateTime(DateTime::createFromFormat('d/m/Y H:i:s', $fromDate)))
                    && $v->getTransactedAt()->isBefore(LocalDateTime::fromDateTime(DateTime::createFromFormat('d/m/Y H:i:s', $toDate)))
                    && $v->getType() == TransactionType::PAYMENT
                    && !in_array($v->getId(), $reversalRelatedTransactionIds);
            },
            ARRAY_FILTER_USE_BOTH
        );

        echo "\$filteredTransactions:" . PHP_EOL;
        var_dump($filteredTransactions);

        if (empty($filteredTransactions)) {
            echo "No transactions found." . PHP_EOL;
        } else {
            $sum = array_reduce(
                $filteredTransactions,
                function ($carry, $item)
                {
                    return $carry->plus($item->getAmount());
                },
                BigDecimal::zero()
            );

            $avg = $sum->dividedBy(BigDecimal::of(sizeof($filteredTransactions)));

            echo "Number of transactions = " . sizeof($filteredTransactions) . PHP_EOL;
            echo "Total Transaction Value = " . number_format($sum->toFloat(), 2) . PHP_EOL;
            echo "Average Transaction Value = " . number_format($avg->toFloat(), 2) . PHP_EOL;
        }


    }

}
