<?php

namespace TransactionAnalyser\Tests;

use Brick\DateTime\LocalDateTime;
use Brick\DateTime\TimeZone;
use Brick\Math\BigDecimal;
use PHPUnit\Framework\Attributes\Before;
use PHPUnit\Framework\Attributes\Test;
use TransactionAnalyser\Transaction;
use TransactionAnalyser\TransactionType;
use PHPUnit\Framework\TestCase;

class TransactionTest extends TestCase
{
    private Transaction|null $transaction = null;

    #[Before]
    function setUp(): void
    {
        $this->transaction = new Transaction(
            "test",
            LocalDateTime::now(TimeZone::utc()),
            BigDecimal::of("5.99"),
            "testMerchant",
            TransactionType::PAYMENT,
            ""
        );
    }


    #[Test]
    public function testNewTransaction()
    {
        $this->assertEquals("test", $this->transaction->getId());
        $this->assertTrue($this->transaction->getTransactedAt()->isBefore(LocalDateTime::now(TimeZone::utc())));
        $this->assertEquals("testMerchant", $this->transaction->getMerchantName());
        $this->assertTrue($this->transaction->getAmount()->isEqualTo(5.99));
        $this->assertEquals(TransactionType::PAYMENT, $this->transaction->getType());
        $this->assertTrue($this->transaction->getRelatedTransactionId() == "");
    }
}
