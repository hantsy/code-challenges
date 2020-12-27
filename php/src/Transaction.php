<?php

namespace TransactionAnalyser;

use Brick\DateTime\LocalDateTime;
use Brick\Math\BigDecimal;

class Transaction
{

    function __construct(
        private string $id,
        private LocalDateTime $transactedAt,
        private BigDecimal $amount,
        private string $merchantName,
        private TransactionType $type,
        private string|null $relatedTransactionId = null)
    {
    }

    function getId(): string
    {
        return $this->id;
    }

    /**
     * @return LocalDateTime
     */
    public function getTransactedAt(): LocalDateTime
    {
        return $this->transactedAt;
    }

    /**
     * @return BigDecimal
     */
    public function getAmount(): BigDecimal
    {
        return $this->amount;
    }

    public function getMerchantName(): string
    {
        return $this->merchantName;
    }

    public function getType(): TransactionType
    {
        return $this->type;
    }

    public function getRelatedTransactionId(): string|null
    {
        return $this->relatedTransactionId;
    }

}
