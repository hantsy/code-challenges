<?php

namespace Hantsy\TransactionAnalyser;

use Brick\DateTime\LocalDateTime;
use Brick\Math\BigDecimal;

class Transaction
{
    private string $id;
    private LocalDateTime $transactedAt;
    private BigDecimal $amount;
    private string $merchantName;
    private TransactionType $type;
    private string|null $relatedTransactionId;

    function __construct(string $id, LocalDateTime $transactedAt, BigDecimal $amount, string $merchantName, TransactionType $type, string|null $relatedTransactionId = null)
    {
        $this->id = $id;
        $this->transactedAt = $transactedAt;
        $this->amount = $amount;
        $this->merchantName = $merchantName;
        $this->type = $type;
        $this->relatedTransactionId = $relatedTransactionId;
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