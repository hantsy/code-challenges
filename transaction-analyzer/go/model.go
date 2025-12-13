package main

import (
	"math/big"
	"time"
)

// TransactionType see: https://github.com/golang/go/issues/19814
type TransactionType string

const (
	PAYMENT  TransactionType = "PAYMENT"
	REVERSAL                 = "REVERSAL"
)

type Transaction struct {
	Id                   string
	TransactedAt         time.Time
	Amount               *big.Float
	MerchantName         string
	Type                 TransactionType
	RelatedTransactionId string
}
