package main

import (
	"math/big"
	"os"
	"reflect"
	"sort"
	"testing"
	"time"
)

func TestBuildTransaction_Valid(t *testing.T) {
	line := "ID1, 20/08/2020 12:45:33, 59.99, Kwik-E-Mart, PAYMENT,"
	tx, err := buildTransaction(line)
	if err != nil {
		t.Fatalf("expected no error, got %v", err)
	}
	if tx.Id != "ID1" {
		t.Fatalf("expected Id ID1, got %s", tx.Id)
	}
	if tx.MerchantName != "Kwik-E-Mart" {
		t.Fatalf("expected Merchant Kwik-E-Mart, got %s", tx.MerchantName)
	}
	if tx.Amount.Text('f', 2) != "59.99" {
		t.Fatalf("amount mismatch, want %s got %s", "59.99", tx.Amount.Text('f', 2))
	}
	if tx.Type != PAYMENT {
		t.Fatalf("type mismatch, want PAYMENT got %s", tx.Type)
	}
}

func TestBuildTransaction_Invalid(t *testing.T) {
	// missing fields
	line := "ID1, 20/08/2020 12:45:33, 59.99"
	_, err := buildTransaction(line)
	if err == nil {
		t.Fatalf("expected error for invalid line, got nil")
	}
}

func TestReadlinesAndLoader(t *testing.T) {
	f, err := os.CreateTemp("", "tx-*.csv")
	if err != nil {
		t.Fatalf("create temp file: %v", err)
	}
	defer os.Remove(f.Name())
	content := "ID, Date, Amount, Merchant, Type, Related Transaction\n"
	content += "A, 20/08/2020 12:00:00, 10.00, Shop, PAYMENT,\n"
	content += "B, 20/08/2020 12:30:00, 5.00, Shop, PAYMENT,\n"
	content += "badline\n"
	if _, err := f.WriteString(content); err != nil {
		t.Fatalf("write temp file: %v", err)
	}
	f.Close()

	lines, err := readlines(f.Name())
	if err != nil {
		t.Fatalf("readlines error: %v", err)
	}
	if len(lines) != 3 { // header dropped, expect 3 lines
		t.Fatalf("expected 3 lines, got %d", len(lines))
	}

	loader := NewTransactionLoader(f.Name())
	txs, err := loader.Load()
	if err != nil {
		t.Fatalf("loader load error: %v", err)
	}
	// only two valid transactions should be returned; badline should be skipped
	if len(txs) != 2 {
		t.Fatalf("expected 2 transactions, got %d", len(txs))
	}
}

type stubLoader struct {
	out []Transaction
}

func (s stubLoader) Load() (result []Transaction, err error) {
	return s.out, nil
}

func TestRepository_QueryByMerchantAndDateRange(t *testing.T) {
	// Prepare some transactions including a reversal referencing the first payment
	baseTime, _ := time.Parse(customDateTimeLayout, "20/08/2020 12:00:00")
	a := Transaction{Id: "A", TransactedAt: baseTime, Amount: big.NewFloat(10.0), MerchantName: "Shop", Type: PAYMENT}
	b := Transaction{Id: "B", TransactedAt: baseTime.Add(30 * time.Minute), Amount: big.NewFloat(5.0), MerchantName: "Shop", Type: PAYMENT}
	// reversal of B
	r := Transaction{Id: "R", TransactedAt: baseTime.Add(35 * time.Minute), Amount: big.NewFloat(-5.0), MerchantName: "Shop", Type: REVERSAL, RelatedTransactionId: "B"}
	loader := stubLoader{out: []Transaction{a, b, r}}
	repo := NewTransactionRepository(loader)

	from := baseTime.Add(-time.Minute)
	to := baseTime.Add(1 * time.Hour)
	res, err := repo.QueryByMerchantAndDateRange("Shop", from, to)
	if err != nil {
		t.Fatalf("query error: %v", err)
	}
	// B should be excluded because it's reversed; A should remain
	ids := []string{res[0].Id}
	if !reflect.DeepEqual(ids, []string{"A"}) {
		t.Fatalf("unexpected result ids: %v", ids)
	}
}

func TestRepository_QueryByMerchantAndDateRange_Table(t *testing.T) {
	cases := []struct {
		name     string
		txs      []Transaction
		merchant string
		from     string
		to       string
		wantIds  []string
	}{
		{
			name: "InclusiveBounds",
			txs: func() []Transaction {
				base, _ := time.Parse(customDateTimeLayout, "20/08/2020 12:00:00")
				return []Transaction{
					{Id: "A", TransactedAt: base, Amount: big.NewFloat(1.0), MerchantName: "Shop", Type: PAYMENT},
					{Id: "B", TransactedAt: base.Add(15 * time.Minute), Amount: big.NewFloat(2.0), MerchantName: "Shop", Type: PAYMENT},
					{Id: "C", TransactedAt: base.Add(30 * time.Minute), Amount: big.NewFloat(3.0), MerchantName: "Shop", Type: PAYMENT},
				}
			}(),
			merchant: "Shop",
			from:     "20/08/2020 12:00:00",
			to:       "20/08/2020 12:30:00",
			wantIds:  []string{"A", "B", "C"},
		},
		{
			name: "ReversalInsideRange",
			txs: func() []Transaction {
				base, _ := time.Parse(customDateTimeLayout, "20/08/2020 12:00:00")
				p := Transaction{Id: "P", TransactedAt: base.Add(5 * time.Minute), Amount: big.NewFloat(10.0), MerchantName: "Shop", Type: PAYMENT}
				r := Transaction{Id: "R", TransactedAt: base.Add(10 * time.Minute), Amount: big.NewFloat(-10.0), MerchantName: "Shop", Type: REVERSAL, RelatedTransactionId: "P"}
				return []Transaction{p, r}
			}(),
			merchant: "Shop",
			from:     "20/08/2020 12:00:00",
			to:       "20/08/2020 13:00:00",
			wantIds:  []string{},
		},
		{
			name: "ReversalOutsideRange",
			txs: func() []Transaction {
				base, _ := time.Parse(customDateTimeLayout, "20/08/2020 12:00:00")
				p := Transaction{Id: "P", TransactedAt: base.Add(5 * time.Minute), Amount: big.NewFloat(10.0), MerchantName: "Shop", Type: PAYMENT}
				r := Transaction{Id: "R", TransactedAt: base.Add(2 * time.Hour), Amount: big.NewFloat(-10.0), MerchantName: "Shop", Type: REVERSAL, RelatedTransactionId: "P"}
				return []Transaction{p, r}
			}(),
			merchant: "Shop",
			from:     "20/08/2020 12:00:00",
			to:       "20/08/2020 13:00:00",
			wantIds:  []string{},
		},
		{
			name: "DifferentMerchantNotAffectedByReversal",
			txs: func() []Transaction {
				base, _ := time.Parse(customDateTimeLayout, "20/08/2020 12:00:00")
				p1 := Transaction{Id: "P1", TransactedAt: base.Add(5 * time.Minute), Amount: big.NewFloat(10.0), MerchantName: "ShopA", Type: PAYMENT}
				p2 := Transaction{Id: "P2", TransactedAt: base.Add(10 * time.Minute), Amount: big.NewFloat(7.0), MerchantName: "ShopB", Type: PAYMENT}
				r := Transaction{Id: "R", TransactedAt: base.Add(20 * time.Minute), Amount: big.NewFloat(-10.0), MerchantName: "ShopA", Type: REVERSAL, RelatedTransactionId: "P1"}
				return []Transaction{p1, p2, r}
			}(),
			merchant: "ShopB",
			from:     "20/08/2020 12:00:00",
			to:       "20/08/2020 13:00:00",
			wantIds:  []string{"P2"},
		},
		{
			name: "FromEqualsTo_SingleInstant",
			txs: func() []Transaction {
				exact, _ := time.Parse(customDateTimeLayout, "20/08/2020 12:00:00")
				return []Transaction{{Id: "X", TransactedAt: exact, Amount: big.NewFloat(1.0), MerchantName: "Shop", Type: PAYMENT}}
			}(),
			merchant: "Shop",
			from:     "20/08/2020 12:00:00",
			to:       "20/08/2020 12:00:00",
			wantIds:  []string{"X"},
		},
	}

	for _, c := range cases {
		t.Run(c.name, func(t *testing.T) {
			loader := stubLoader{out: c.txs}
			repo := NewTransactionRepository(loader)
			from, _ := time.Parse(customDateTimeLayout, c.from)
			to, _ := time.Parse(customDateTimeLayout, c.to)
			got, err := repo.QueryByMerchantAndDateRange(c.merchant, from, to)
			if err != nil {
				t.Fatalf("query error: %v", err)
			}
			var gotIds []string
			for _, v := range got {
				gotIds = append(gotIds, v.Id)
			}
			if gotIds == nil {
				gotIds = []string{}
			}
			sort.Strings(gotIds)
			sort.Strings(c.wantIds)
			if !reflect.DeepEqual(gotIds, c.wantIds) {
				t.Fatalf("case %s: expected ids %v got %v", c.name, c.wantIds, gotIds)
			}
		})
	}
}
