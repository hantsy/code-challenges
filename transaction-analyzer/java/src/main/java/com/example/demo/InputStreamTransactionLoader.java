package com.example.demo;

import com.example.demo.Transaction;
import com.example.demo.TransactionLoader;
import com.example.demo.TransactionType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public final class InputStreamTransactionLoader implements TransactionLoader {
    final InputStream source;

    public InputStreamTransactionLoader(InputStream source) {
        this.source = source;
    }

    @Override
    public List<Transaction> load() throws IOException {
        try (var reader = new BufferedReader(new InputStreamReader(this.source))) {
            return reader.lines().skip(1).map(this::buildTransaction).collect(Collectors.toList());
        }
    }

    private Transaction buildTransaction(String line) {
        System.out.println("reading line:" + line);
        var fields = line.split(",");
        System.out.println("fields: " + fields.length);
        return new Transaction(
                fields[0].trim(),
                LocalDateTime.parse(fields[1].trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                new BigDecimal(fields[2].trim()),
                fields[3].trim(),
                TransactionType.valueOf(fields[4].trim()),
                fields.length == 6 ? fields[5].trim() : null
        );
    }
}
