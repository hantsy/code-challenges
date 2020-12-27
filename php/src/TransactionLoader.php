<?php

namespace TransactionAnalyser;

use Brick\DateTime\LocalDateTime;
use Brick\Math\BigDecimal;
use DateTime;

class TransactionLoader implements TransactionLoaderInterface
{

    private string $csvFile;

    function __construct(string $file)
    {
        $this->csvFile = $file;
    }

    function load(): array
    {
        echo "loading file: " . PHP_EOL;
        var_dump($this->csvFile);
        echo PHP_EOL;

        $handle = fopen($this->csvFile, "r");

        $transactions = [];
        $firstline = true;
        while ($data = fgetcsv($handle, 1000, ",")) {
            echo "read line:" . implode(", ", $data) . PHP_EOL;
            if (!$firstline) {
                $data = array_map("utf8_encode", $data); //added
                $num = count($data);
                $id = trim($data[0]);
                $transactedAt = LocalDateTime::fromDateTime(DateTime::createFromFormat('d/m/Y H:i:s', trim($data[1])));//'dd/MM/yyyy HH:mm:ss',
                $mount = BigDecimal::of(trim($data[2]));
                $merchant = trim($data[3]);
                $type = new TransactionType(trim($data[4]));
                $reversalTransactionId = null;
                if ($num == 6) {
                    $reversalTransactionId = trim($data[5]);
                }
                $transactions[] = new Transaction($id, $transactedAt, $mount, $merchant, $type, $reversalTransactionId);
            }
            $firstline = false;
        }

        return $transactions;
    }

}