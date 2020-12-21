<?php


namespace Hantsy\TransactionAnalyser;


use Brick\DateTime\LocalDateTime;
use DateTime;

class TransactionRepository
{
    /**
     * @var TransactionLoader
     */
    private TransactionLoader $loader;

    private array $transactions;

    public function __construct(TransactionLoader $loader)
    {
        $this->loader = $loader;
        $this->transactions = $loader->load();
    }

    public function queryByMerchantAndDateRange(
        string $merchant,
        string $fromDate,
        string $toDate
    ): array
    {
        //var_dump($transactions);
        // generating report.
        $reversalTransactions = array_filter($this->transactions,
            function ($v, $k) {
                return $v->getType() == TransactionType::REVERSAL;
            },
            ARRAY_FILTER_USE_BOTH
        );

//        echo "\$reversalTransactions:" . PHP_EOL;
//        var_dump($reversalTransactions);

        $reversalRelatedTransactionIds = array_map(
            function ($v) {
                return $v->getRelatedTransactionId();
            },
            $reversalTransactions
        );

//        echo "\$reversalRelatedTransactionIds:" . PHP_EOL;
//        var_dump($reversalRelatedTransactionIds);

        return array_filter($this->transactions,
            callback: function ($v, $k) use ($reversalRelatedTransactionIds, $toDate, $fromDate, $merchant) {
                return $v->getMerchantName() == $merchant
                    && $v->getTransactedAt()->isAfter(LocalDateTime::fromDateTime(DateTime::createFromFormat('d/m/Y H:i:s', $fromDate)))
                    && $v->getTransactedAt()->isBefore(LocalDateTime::fromDateTime(DateTime::createFromFormat('d/m/Y H:i:s', $toDate)))
                    && $v->getType() == TransactionType::PAYMENT
                    && !in_array($v->getId(), $reversalRelatedTransactionIds);
            },
            mode: ARRAY_FILTER_USE_BOTH
        );
    }
}