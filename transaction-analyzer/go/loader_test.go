package main

import (
	"math/big"
	"testing"
)

func TestFileTransactionLoader_Load_ParsesValidCSV(t *testing.T) {
	content := "ID, Date, Amount, Merchant, Type, Related Transaction\n"
	content += "T1, 20/08/2020 12:00:00, 42.50, ShopA, PAYMENT,\n"
	content += "T2, 20/08/2020 12:15:00, 10.00, ShopB, PAYMENT,\n"
	loader, cleanup := createLoaderWithCSV(t, content)
	defer cleanup()
	txs, err := loader.Load()
	if err != nil {
		t.Fatalf("loader load error: %v", err)
	}
	if len(txs) != 2 {
		t.Fatalf("expected 2 transactions, got %d", len(txs))
	}
	if txs[0].Id != "T1" || txs[1].Id != "T2" {
		t.Fatalf("unexpected ids: %v", []string{txs[0].Id, txs[1].Id})
	}
	if txs[0].Amount.Text('f', 2) != "42.50" {
		t.Fatalf("unexpected amount: got %s", txs[0].Amount.Text('f', 2))
	}
}

func TestFileTransactionLoader_Load_SkipsMalformedLines(t *testing.T) {
	content := "ID, Date, Amount, Merchant, Type, Related Transaction\n"
	content += "GOOD, 20/08/2020 12:00:00, 10.00, Shop, PAYMENT,\n"
	content += "badline\n"
	loader, cleanup := createLoaderWithCSV(t, content)
	defer cleanup()
	txs, err := loader.Load()
	if err != nil {
		t.Fatalf("loader load error: %v", err)
	}
	if len(txs) != 1 {
		t.Fatalf("expected 1 transaction, got %d", len(txs))
	}
	if txs[0].Id != "GOOD" {
		t.Fatalf("unexpected id: %s", txs[0].Id)
	}
	if txs[0].Amount.Cmp(big.NewFloat(10.0)) != 0 {
		t.Fatalf("unexpected amount value: %s", txs[0].Amount.Text('f', 2))
	}
}
