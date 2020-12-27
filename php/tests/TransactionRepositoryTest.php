<?php

namespace TransactionAnalyser\Tests;

use Brick\DateTime\LocalDateTime;
use DateTime;
use TransactionAnalyser\TransactionLoader;
use PHPUnit\Framework\TestCase;
use TransactionAnalyser\TransactionLoaderInterface;
use TransactionAnalyser\TransactionRepository;

class TransactionRepositoryTest extends TestCase
{
    public function testTransactionRepositoryAgainstFakeLoader()
    {
        $loader = new FakeTransactionLoader();
        $repository = new TransactionRepository($loader);
        $transactions = $repository->queryByMerchantAndDateRange(
            "Kwik-E-Mart",
            LocalDateTime::fromDateTime(DateTime::createFromFormat('d/m/Y H:i:s', "20/08/2020 12:00:00")),
            LocalDateTime::fromDateTime(DateTime::createFromFormat('d/m/Y H:i:s', "20/08/2020 13:00:00"))
        );

        $this->assertEquals(1, sizeof($transactions));
    }

    public function testTransactionRepositoryAgainstFakeLoaderMock()
    {
        $stub = $this->createMock(TransactionLoaderInterface::class);
        $stub->method("load")->willReturn(Fixtures::transactionData());

        $repository = new TransactionRepository($stub);
        $transactions = $repository->queryByMerchantAndDateRange(
            "Kwik-E-Mart",
            LocalDateTime::fromDateTime(DateTime::createFromFormat('d/m/Y H:i:s', "20/08/2020 12:00:00")),
            LocalDateTime::fromDateTime(DateTime::createFromFormat('d/m/Y H:i:s', "20/08/2020 13:00:00"))
        );
        $this->assertEquals(1, sizeof($transactions));
    }

}
