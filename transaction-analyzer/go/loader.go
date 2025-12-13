package main

import (
	"bufio"
	"encoding/csv"
	"errors"
	"fmt"
	"io"
	"log"
	"math/big"
	"os"
	"strings"
	"time"
)

const customDateTimeLayout = "02/01/2006 15:04:05"

type TransactionLoader interface {
	Load() (result []Transaction, err error)
}

type FileTransactionLoader struct {
	file string
}

func NewTransactionLoader(file string) TransactionLoader {
	return &FileTransactionLoader{file: file}
}

func (t FileTransactionLoader) Load() (result []Transaction, err error) {
	f, err := os.Open(t.file)
	if err != nil {
		return nil, err
	}
	defer f.Close()
	r := csv.NewReader(f)
	// read and drop header
	if _, err := r.Read(); err != nil {
		if err == io.EOF {
			return nil, nil
		}
		return nil, err
	}
	for {
		record, err := r.Read()
		if err != nil {
			if err == io.EOF {
				break
			}
			var perr *csv.ParseError
			if errors.As(err, &perr) {
				// skip malformed CSV records
				log.Printf("skipping invalid CSV record: %v; err: %v", perr, err)
				continue
			}
			return nil, err
		}
		transaction, terr := buildTransactionFromFields(record)
		if terr != nil {
			log.Printf("skipping invalid transaction line: %v; err: %v", record, terr)
			continue
		}
		result = append(result, transaction)
	}
	return
}

func buildTransaction(value string) (Transaction, error) {
	// parse a single CSV line
	r := csv.NewReader(strings.NewReader(value))
	record, err := r.Read()
	if err != nil {
		return Transaction{}, err
	}
	return buildTransactionFromFields(record)
}

func buildTransactionFromFields(fields []string) (Transaction, error) {
	if len(fields) < 6 {
		return Transaction{}, fmt.Errorf("invalid transaction fields: %v", fields)
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
