<?php

namespace TransactionAnalyser;

use Brick\DateTime\LocalDateTime;

interface TransactionRepositoryInterface
{
    public function queryByMerchantAndDateRange(string $merchant, LocalDateTime $fromDate, LocalDateTime $toDate): array;
}