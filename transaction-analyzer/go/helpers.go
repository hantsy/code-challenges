package main

import (
	"math/big"
	"strings"
)

// formatFloatTwoDecimals formats a big.Float to a decimal string with two places.
func formatFloatTwoDecimals(f *big.Float) string {
	return f.Text('f', 2)
}

// containsString returns true when s exists in the slice.
func containsString(slice []string, s string) bool {
	if slice == nil {
		return false
	}
	for _, v := range slice {
		if strings.TrimSpace(v) == strings.TrimSpace(s) {
			return true
		}
	}
	return false
}
