package main

import (
	"os"
	"testing"
)

// createTempCSV writes content to a temp file and returns the path and cleanup function.
func createTempCSV(t *testing.T, content string) (string, func()) {
	t.Helper()
	f, err := os.CreateTemp("", "tx-*.csv")
	if err != nil {
		t.Fatalf("create temp file: %v", err)
	}
	if _, err := f.WriteString(content); err != nil {
		f.Close()
		os.Remove(f.Name())
		t.Fatalf("write temp file: %v", err)
	}
	f.Close()
	return f.Name(), func() { os.Remove(f.Name()) }
}

// createLoaderWithCSV creates a FileTransactionLoader backed by a temp CSV and returns cleanup
func createLoaderWithCSV(t *testing.T, content string) (TransactionLoader, func()) {
	t.Helper()
	path, cleanup := createTempCSV(t, content)
	return NewTransactionLoader(path), cleanup
}

// loaderStub is a small test stub that implements TransactionLoader.
type loaderStub struct {
	txs []Transaction
}

func (l loaderStub) Load() (result []Transaction, err error) { return l.txs, nil }
