package main

import (
	"math/big"
	"testing"
	"time"
)

// loaderStub is provided by testutil.go

func TestInMemoryRepository_ExcludesReversedPayments(t *testing.T) {
	base, _ := time.Parse(customDateTimeLayout, "20/08/2020 12:00:00")
	// Payment P1 in range
	p1 := Transaction{Id: "P1", TransactedAt: base.Add(5 * time.Minute), Amount: big.NewFloat(10), MerchantName: "Shop", Type: PAYMENT}
	// Reversal R1 references P1 but occurs outside the range
	r1 := Transaction{Id: "R1", TransactedAt: base.Add(2 * time.Hour), Amount: big.NewFloat(-10), MerchantName: "Shop", Type: REVERSAL, RelatedTransactionId: "P1"}
	loader := loaderStub{txs: []Transaction{p1, r1}}
	repo := NewTransactionRepository(loader)
	res, err := repo.QueryByMerchantAndDateRange("Shop", base, base.Add(1*time.Hour))
	if err != nil {
		t.Fatalf("query err: %v", err)
	}
	if len(res) != 0 {
		t.Fatalf("expected 0 results but got %d", len(res))
	}
}

func TestInMemoryRepository_IncludesPaymentIfNotReversed(t *testing.T) {
	base, _ := time.Parse(customDateTimeLayout, "20/08/2020 12:00:00")
	p1 := Transaction{Id: "P1", TransactedAt: base.Add(5 * time.Minute), Amount: big.NewFloat(10), MerchantName: "Shop", Type: PAYMENT}
	loader := loaderStub{txs: []Transaction{p1}}
	repo := NewTransactionRepository(loader)
	res, err := repo.QueryByMerchantAndDateRange("Shop", base, base.Add(1*time.Hour))
	if err != nil {
		t.Fatalf("query err: %v", err)
	}
	if len(res) != 1 {
		t.Fatalf("expected 1 result but got %d", len(res))
	}
	if res[0].Id != "P1" {
		t.Fatalf("unexpected id: %s", res[0].Id)
	}
}

func TestInMemoryRepository_DateBoundsInclusive(t *testing.T) {
	base, _ := time.Parse(customDateTimeLayout, "20/08/2020 12:00:00")
	p := Transaction{Id: "P", TransactedAt: base, Amount: big.NewFloat(1), MerchantName: "Shop", Type: PAYMENT}
	loader := loaderStub{txs: []Transaction{p}}
	repo := NewTransactionRepository(loader)
	res, err := repo.QueryByMerchantAndDateRange("Shop", base, base)
	if err != nil {
		t.Fatalf("query err: %v", err)
	}
	if len(res) != 1 {
		t.Fatalf("expected 1 result but got %d", len(res))
	}
}
