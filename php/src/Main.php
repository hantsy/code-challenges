<?php

namespace TransactionAnalyser;

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

        //define repository
        $repository = new TransactionRepository($loader);

        //query result
        $filteredTransactions = $repository->queryByMerchantAndDateRange(
            $merchant,
            LocalDateTime::fromDateTime(DateTime::createFromFormat('d/m/Y H:i:s', $fromDate)),
            LocalDateTime::fromDateTime(DateTime::createFromFormat('d/m/Y H:i:s', $toDate))
        );

        echo "\$filteredTransactions:" . PHP_EOL;
        var_dump($filteredTransactions);

        if (empty($filteredTransactions)) {
            echo "No transactions found." . PHP_EOL;
        } else {
            $sum = array_reduce(
                $filteredTransactions,
                function ($carry, $item) {
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
