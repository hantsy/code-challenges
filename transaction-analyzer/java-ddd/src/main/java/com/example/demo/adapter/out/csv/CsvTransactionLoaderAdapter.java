package com.example.demo.adapter.out.csv;

import com.example.demo.port.out.TransactionLoader;
import com.example.demo.domain.model.Transaction;
import com.example.demo.domain.model.TransactionType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CsvTransactionLoaderAdapter implements TransactionLoader {
    private static final Logger LOGGER = Logger.getLogger(CsvTransactionLoaderAdapter.class.getName());
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final InputStream source;

    public CsvTransactionLoaderAdapter(InputStream source) {
        this.source = source;
    }

    @Override
    public List<Transaction> load() {
        try (var reader = new BufferedReader(new InputStreamReader(this.source))) {
            return reader.lines().skip(1).map(this::buildTransaction).toList();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "failed to load transactions from csv", e);
        }
        return Collections.emptyList();
    }

    private Transaction buildTransaction(String line) {
        var fields = line.split(",");
        return new Transaction(
                fields[0].trim(),
                LocalDateTime.parse(fields[1].trim(), DATE_FORMAT),
                new BigDecimal(fields[2].trim()),
                fields[3].trim(),
                TransactionType.valueOf(fields[4].trim()),
                fields.length == 6 ? fields[5].trim() : null
        );
    }
}
