<?php

namespace TransactionAnalyser\Tests;

use TransactionAnalyser\TransactionType;
use PHPUnit\Framework\TestCase;

class TransactionTypeTest extends TestCase
{
    public function testTransactionType()
    {
        $payment ="PAYMENT";
        $this->assertEquals(TransactionType::PAYMENT, TransactionType::from($payment));
        $reversal ="REVERSAL";
        $this->assertEquals(TransactionType::REVERSAL, TransactionType::from($reversal));
    }
}
