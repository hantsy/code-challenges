package main

import (
	"bufio"
	"flag"
	"fmt"
	"log"
	"math/big"
	"os"
	"strings"
	"time"
	// removed dependency on external slice helper: we implement containsString below
)

const customDateTimeLayout = "02/01/2006 15:04:05"

func NewTransactionRepository(loader TransactionLoader) TransactionRepository {
	return &InMemoryTransactionRepository{
		loader: loader,
	}
}

type TransactionRepository interface {
	QueryByMerchantAndDateRange(merchant string,
		fromDate time.Time,
		toDate time.Time) (result []Transaction, err error)
}

type InMemoryTransactionRepository struct {
	loader TransactionLoader
}

func (t *InMemoryTransactionRepository) QueryByMerchantAndDateRange(
	merchant string,
	fromDate time.Time,
	toDate time.Time) (result []Transaction, err error) {
	fmt.Println("calling QueryByMerchantAndDateRange", merchant, fromDate, toDate)

	//load transaction from csv file
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

func NewTransactionLoader(file string) TransactionLoader {
	return &FileTransactionLoader{
		file: file,
	}
}

type TransactionLoader interface {
	Load() (result []Transaction, err error)
}

type FileTransactionLoader struct {
	file string
}

func (t FileTransactionLoader) Load() (result []Transaction, err error) {
	lines, err := readlines(t.file)
	if err != nil {
		return nil, err
	}
	for _, value := range lines {
		transaction, terr := buildTransaction(value)
		if terr != nil {
			// skip malformed lines but continue loading others
			log.Printf("skipping invalid transaction line: %v; err: %v", value, terr)
			continue
		}
		result = append(result, transaction)
	}
	return
}

func buildTransaction(value string) (Transaction, error) {
	fields := strings.Split(value, ",")
	if len(fields) < 6 {
		return Transaction{}, fmt.Errorf("invalid transaction line: %q", value)
	}
	transactedAt, err := time.Parse(customDateTimeLayout, strings.TrimSpace(fields[1]))
	if err != nil {
		return Transaction{}, fmt.Errorf("parse time: %w", err)
	}
	amount, _, err := new(big.Float).Parse(strings.TrimSpace(fields[2]), 10)
	if err != nil {
		return Transaction{}, fmt.Errorf("parse amount: %w", err)
	}
	transaction := Transaction{
		Id:                   strings.TrimSpace(fields[0]),
		TransactedAt:         transactedAt,
		Amount:               amount,
		MerchantName:         strings.TrimSpace(fields[3]),
		Type:                 TransactionType(strings.TrimSpace(fields[4])),
		RelatedTransactionId: strings.TrimSpace(fields[5]),
	}
	return transaction, nil
}

// readlines read file content line by line, but skip the header line.
func readlines(file string) (lines []string, err error) {
	f, err := os.Open(file)
	if err != nil {
		return nil, err
	}
	defer f.Close()
	rd := bufio.NewScanner(f)
	// skip header line
	if rd.Scan() {
		// dropped header
	}
	for rd.Scan() {
		lines = append(lines, strings.TrimSpace(rd.Text()))
	}
	if err := rd.Err(); err != nil {
		return nil, err
	}
	return
}

// containsString returns true when s exists in the slice.
func containsString(slice []string, s string) bool {
	for _, v := range slice {
		if v == s {
			return true
		}
	}
	return false
}

// formatFloatTwoDecimals formats a big.Float to a decimal string with two places.
func formatFloatTwoDecimals(f *big.Float) string {
	// Use Text with 'f' and precision 2 for reliable formatting
	return f.Text('f', 2)
}

// TransactionType see: https://github.com/golang/go/issues/19814
type TransactionType string

const (
	PAYMENT  TransactionType = "PAYMENT"
	REVERSAL TransactionType = "REVERSAL"
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
	// support non-interactive stdin file for debugging
	stdinFile := flag.String("stdin-file", "", "path to a file to use as stdin input (optional)")
	fromFlag := flag.String("from", "", "fromDate (dd/MM/yyyy HH:mm:ss)")
	toFlag := flag.String("to", "", "toDate (dd/MM/yyyy HH:mm:ss)")
	merchantFlag := flag.String("merchant", "", "merchant name")
	flag.Parse()
	if *stdinFile != "" {
		f, err := os.Open(*stdinFile)
		if err != nil {
			fmt.Println("cannot open stdin file:", err)
			os.Exit(1)
		}
		os.Stdin = f
		// do not close here; leaving open until program exit
	}

	// user input or flags
	var fromDate string
	var toDate string
	var merchant string
	if *fromFlag != "" || *toFlag != "" || *merchantFlag != "" {
		// if any flag provided, require all
		if *fromFlag == "" || *toFlag == "" || *merchantFlag == "" {
			fmt.Println("when using flags: --from, --to and --merchant must all be provided")
			os.Exit(1)
		}
		fromDate = *fromFlag
		toDate = *toFlag
		merchant = *merchantFlag
	} else {
		// read from stdin interactively
		scanner := bufio.NewScanner(os.Stdin)
		fmt.Println("fromDate (dd/MM/yyyy HH:mm:ss):")
		scanner.Scan()
		fromDate = scanner.Text()
		fmt.Println("toDate (dd/MM/yyyy HH:mm:ss):")
		scanner.Scan()
		toDate = scanner.Text()
		fmt.Println("merchant:")
		scanner.Scan()
		merchant = scanner.Text()
	}

	//print all input data.
	fmt.Println("all input data", fromDate, toDate, merchant)
	parsedFromDate, err := time.Parse(customDateTimeLayout, fromDate)
	if err != nil {
		fmt.Println("invalid fromDate format:", err)
		os.Exit(1)
	}
	parsedToDate, err := time.Parse(customDateTimeLayout, toDate)
	if err != nil {
		fmt.Println("invalid toDate format:", err)
		os.Exit(1)
	}
	loader := NewTransactionLoader("./input.csv")
	repository := NewTransactionRepository(loader)
	filtered, err := repository.QueryByMerchantAndDateRange(merchant, parsedFromDate, parsedToDate)
	if err != nil {
		fmt.Println("error loading transactions:", err)
		os.Exit(1)
	}

	fmt.Println("filtered transactions:", filtered)

	ln := len(filtered)
	if ln > 0 {
		sum := big.NewFloat(0.0)
		for _, v := range filtered {
			sum = sum.Add(sum, v.Amount)
		}

		avg := new(big.Float).Quo(sum, big.NewFloat(float64(ln)))
		fmt.Printf("Number of transactions:%d \n", ln)
		fmt.Printf("Total transaction value is:%s \n", formatFloatTwoDecimals(sum))
		fmt.Printf("Average transaction value is:%s \n", formatFloatTwoDecimals(avg))
	} else {
		fmt.Println("No Transactions found.")
	}

}
