<?php

namespace TransactionAnalyser\Tests;

use PHPUnit\Framework\Attributes\Test;
use TransactionAnalyser\TransactionType;
use PHPUnit\Framework\TestCase;

class TransactionTypeTest extends TestCase
{
    #[Test]
    public function testTransactionType()
    {
        $payment = "PAYMENT";
        $this->assertEquals(TransactionType::PAYMENT, TransactionType::from($payment));
        $reversal = "REVERSAL";
        $this->assertEquals(TransactionType::REVERSAL, TransactionType::from($reversal));
    }
}
