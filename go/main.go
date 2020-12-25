package main

import (
	"bufio"
	"fmt"
	"io"
	"log"
	"math/big"
	"os"
	"strings"
	"time"

	slice "github.com/stretchr/stew/slice"
)

func NewTransactionRepository(loader TransactionLoaderInterface) TransactionRepositoryInterface {
	return &TransactionRepository{
		loader: loader,
	}
}

type TransactionRepositoryInterface interface {
	QueryByMerchantAndDateRange(merchant string,
		fromDate time.Time,
		toDate time.Time) []Transaction
}

type TransactionRepository struct {
	loader TransactionLoaderInterface
}

func (t TransactionRepository) QueryByMerchantAndDateRange(
	merchant string,
	fromDate time.Time,
	toDate time.Time) []Transaction {
	var relatedIds []string
	transactions, _ := t.loader.Load()
	for _, value := range transactions {
		if value.Type == REVERSAL {
			relatedIds = append(relatedIds, value.RelatedTransactionId)
		}
	}

	var filtered []Transaction
	for _, value := range transactions {
		if value.MerchantName == merchant && value.Type == PAYMENT && value.TransactedAt.Before(toDate) && value.TransactedAt.After(fromDate) && slice.ContainsString(relatedIds, value.Id) {
			filtered = append(filtered, value)
		}
	}
	return filtered
}

func NewTransactionLoader(file string) TransactionLoaderInterface {
	return &TransactionLoader{
		file: file,
	}
}

type TransactionLoaderInterface interface {
	Load() (result []Transaction, err error)
}

type TransactionLoader struct {
	file string
}

func (t TransactionLoader) Load() (result []Transaction, err error) {
	f, err := os.OpenFile(t.file, os.O_RDONLY, os.ModePerm)
	if err != nil {
		log.Fatalf("open file error: %v", err)
		return
	}
	defer f.Close()

	var lines []string
	rd := bufio.NewReader(f)
	firstline := true
	for {
		if !firstline {
			line, err := rd.ReadString('\n')
			if err != nil {
				if err == io.EOF {
					break
				}

				log.Fatalf("read file line error: %v", err)
				return nil, err
			}
			str := strings.TrimSpace(line)
			lines = append(lines, str)
		}
		firstline = false
	}

	var transactions []Transaction
	for _, value := range lines {
		fields := strings.Split(value, ",")

		transactedAt, _ := time.Parse("20/08/2020 14:07:10", fields[1])
		amount, _ := new(big.Float).SetPrec(2).SetString(fields[1])
		transaction := Transaction{
			Id:                   fields[0],
			TransactedAt:         transactedAt,
			Amount:               amount,
			MerchantName:         fields[3],
			Type:                 TransactionType(fields[4]),
			RelatedTransactionId: fields[5],
		}
		transactions = append(transactions, transaction)
	}
	return transactions, nil
}

// https://github.com/golang/go/issues/19814
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

func main() {
	fmt.Println("Go Go Go!")
	var merchant string
	var fromDate string
	var toDate string
	fmt.Println("fromDate (dd/MM/yyyy HH:mm:ss):")
	fmt.Scanf("%s", &fromDate)
	fmt.Println("toDate (dd/MM/yyyy HH:mm:ss):")
	fmt.Scanf("%s", &toDate)
	fmt.Println("merchant")
	fmt.Scanf("%s", &merchant)

	//print all input data.
	fmt.Println("all input data", fromDate, toDate, merchant)
	parsedFromDate, _ := time.Parse("20/08/2020 14:07:10", fromDate)
	parsedToDate, _ := time.Parse("20/08/2020 14:07:10", toDate)
	var filtered = NewTransactionRepository(NewTransactionLoader("./input.csv")).QueryByMerchantAndDateRange(merchant, parsedFromDate, parsedToDate)

	fmt.Println("filtered transactions:", filtered)

	ln := len(filtered)
	if ln > 0 {
		sum := big.NewFloat(0.0)
		for _, v := range filtered {
			sum = sum.Add(sum, v.Amount)
		}

		avg := new(big.Float).Quo(sum, big.NewFloat(float64(ln)))
		fmt.Printf("Number of tranactions:%d", ln)
		fmt.Printf("Total transaction value is:%f", sum)
		fmt.Printf("Average transaction value is:%f", avg)
	}
}
