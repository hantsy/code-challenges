package com.example.demo.domain.service;

import com.example.demo.port.in.LoadTransactionRecordsFromCsvPort;
import com.example.demo.port.out.TransactionLoader;
import com.example.demo.port.out.TransactionPersister;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionLoadService implements LoadTransactionRecordsFromCsvPort {
    private static final Logger LOGGER = Logger.getLogger(TransactionLoadService.class.getName());

    private final TransactionLoader loader;
    private final TransactionPersister persister;

    public TransactionLoadService(TransactionLoader loader, TransactionPersister persister) {
        this.loader = loader;
        this.persister = persister;
    }

    @Override
    public void loadAndPersist() {
        var transactions = this.loader.load();
        LOGGER.log(Level.INFO, "{0} transactions loaded from csv files", transactions.size());
        this.persister.persist(transactions);
    }
}
