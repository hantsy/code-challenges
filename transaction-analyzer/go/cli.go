package main

import (
	"bufio"
	"flag"
	"fmt"
	"math/big"
	"os"
	"time"
)

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

	//print all input data
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
