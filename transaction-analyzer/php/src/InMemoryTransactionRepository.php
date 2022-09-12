<?php


namespace TransactionAnalyser;


use Brick\DateTime\LocalDateTime;
use DateTime;

class InMemoryTransactionRepository implements TransactionRepositoryInterface
{
    /**
     * @var TransactionLoaderInterface
     */
    private TransactionLoaderInterface $loader;

    private array $transactions;

    public function __construct(TransactionLoaderInterface $loader)
    {
        $this->loader = $loader;
        $this->transactions = $loader->load();
    }

    public function queryByMerchantAndDateRange(
        string $merchant,
        LocalDateTime $fromDate,
        LocalDateTime $toDate
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
                    && $v->getTransactedAt()->isAfter($fromDate)
                    && $v->getTransactedAt()->isBefore($toDate)
                    && $v->getType() == TransactionType::PAYMENT
                    && !in_array($v->getId(), $reversalRelatedTransactionIds);
            },
            mode: ARRAY_FILTER_USE_BOTH
        );
    }
}