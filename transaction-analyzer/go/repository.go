package main

import (
	"fmt"
	"time"
)

type TransactionRepository interface {
	QueryByMerchantAndDateRange(merchant string,
		fromDate time.Time,
		toDate time.Time) (result []Transaction, err error)
}

type InMemoryTransactionRepository struct {
	loader TransactionLoader
}

func NewTransactionRepository(loader TransactionLoader) TransactionRepository {
	return &InMemoryTransactionRepository{loader: loader}
}

func (t *InMemoryTransactionRepository) QueryByMerchantAndDateRange(
	merchant string,
	fromDate time.Time,
	toDate time.Time) (result []Transaction, err error) {
	fmt.Println("calling QueryByMerchantAndDateRange", merchant, fromDate, toDate)

	// load transactions from csv file
	transactions, err := t.loader.Load()
	if err != nil {
		return nil, err
	}

	// filtered related transactions
	var relatedIds []string
	for _, value := range transactions {
		if value.Type == REVERSAL {
			relatedIds = append(relatedIds, value.RelatedTransactionId)
		}
	}
	fmt.Println("reversal related ids:", relatedIds)
	for _, value := range transactions {
		if value.MerchantName == merchant &&
			value.Type == PAYMENT &&
			// include transactions that are within the date range, inclusive
			!value.TransactedAt.After(toDate) &&
			!value.TransactedAt.Before(fromDate) &&
			!containsString(relatedIds, value.Id) {
			result = append(result, value)
		}
	}
	return
}

// containsString is implemented in helpers.go
