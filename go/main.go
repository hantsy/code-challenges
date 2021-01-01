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

const customDateTimeLayout = "02/01/2006 15:04:05"

func NewTransactionRepository(loader TransactionLoaderInterface) TransactionRepositoryInterface {
	return &TransactionRepository{
		loader: loader,
	}
}

type TransactionRepositoryInterface interface {
	QueryByMerchantAndDateRange(merchant string,
		fromDate time.Time,
		toDate time.Time) (result []Transaction, err error)
}

type TransactionRepository struct {
	loader TransactionLoaderInterface
}

func (t TransactionRepository) QueryByMerchantAndDateRange(
	merchant string,
	fromDate time.Time,
	toDate time.Time) (result []Transaction, err error) {
	fmt.Println("calling QueryByMerchantAndDateRange", merchant, fromDate, toDate)

	//load transaction from csv file
	transactions, err := t.loader.Load()
	fmt.Println("loaded transactions:", transactions)

	// filtered related transactions
	var relatedIds []string
	for _, value := range transactions {
		if value.Type == REVERSAL {
			relatedIds = append(relatedIds, value.RelatedTransactionId)
		}
	}
	fmt.Println("reversal related ids:", relatedIds)
	for _, value := range transactions {
		if value.MerchantName == merchant && value.Type == PAYMENT && value.TransactedAt.Before(toDate) && value.TransactedAt.After(fromDate) && !slice.ContainsString(relatedIds, value.Id) {
			result = append(result, value)
		}
	}
	return
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
	lines, _ := readlines(t.file)
	for _, value := range lines {
		transaction := buildTransaction(value)
		result = append(result, transaction)
	}
	return
}

func buildTransaction(value string) Transaction {
	fields := strings.Split(value, ",")

	transactedAt, _ := time.Parse(customDateTimeLayout, strings.TrimSpace(fields[1]))
	amount, _, _ := new(big.Float).Parse(strings.TrimSpace(fields[2]), 10)
	transaction := Transaction{
		Id:                   strings.TrimSpace(fields[0]),
		TransactedAt:         transactedAt,
		Amount:               amount,
		MerchantName:         strings.TrimSpace(fields[3]),
		Type:                 TransactionType(strings.TrimSpace(fields[4])),
		RelatedTransactionId: strings.TrimSpace(fields[5]),
	}
	return transaction
}

//readlines read file content line by line, but skip the header line.
func readlines(file string) (lines []string, err error) {
	f, err := os.OpenFile(file, os.O_RDONLY, os.ModePerm)
	if err != nil {
		log.Fatalf("open file error: %v", err)
		return
	}
	defer f.Close()
	rd := bufio.NewReader(f)

	//skip csv header line.
	firstline := true
	for {
		line, err := rd.ReadString('\n')
		if err != nil {
			if err == io.EOF {
				break
			}

			log.Fatalf("read file line error: %v", err)
			return nil, err
		}

		if firstline == false {
			str := strings.TrimSpace(line)
			lines = append(lines, str)
		}

		firstline = false
	}
	return
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

	// user input.
	scanner := bufio.NewScanner(os.Stdin)
	fmt.Println("fromDate (dd/MM/yyyy HH:mm:ss):")
	scanner.Scan()
	fromDate := scanner.Text()
	fmt.Println("toDate (dd/MM/yyyy HH:mm:ss):")
	scanner.Scan()
	toDate := scanner.Text()
	fmt.Println("merchant")
	scanner.Scan()
	merchant := scanner.Text()

	//print all input data.
	fmt.Println("all input data", fromDate, toDate, merchant)
	parsedFromDate, _ := time.Parse(customDateTimeLayout, fromDate)
	parsedToDate, _ := time.Parse(customDateTimeLayout, toDate)
	loader := NewTransactionLoader("./input.csv")
	repository := NewTransactionRepository(loader)
	filtered, _ := repository.QueryByMerchantAndDateRange(merchant, parsedFromDate, parsedToDate)

	fmt.Println("filtered transactions:", filtered)

	ln := len(filtered)
	if ln > 0 {
		sum := big.NewFloat(0.0)
		for _, v := range filtered {
			sum = sum.Add(sum, v.Amount)
		}

		avg := new(big.Float).Quo(sum, big.NewFloat(float64(ln)))
		fmt.Printf("Number of tranactions:%d \n", ln)
		fmt.Printf("Total transaction value is:%.2f \n", sum)
		fmt.Printf("Average transaction value is:%.2f \n", avg)
	}
}
