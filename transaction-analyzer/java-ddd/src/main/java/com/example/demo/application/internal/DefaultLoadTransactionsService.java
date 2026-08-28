package com.example.demo.application.internal;

import com.example.demo.application.LoadTransactionsService;
import com.example.demo.domain.repository.TransactionRepository;
import com.example.demo.domain.service.TransactionLoader;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultLoadTransactionsService implements LoadTransactionsService {
    private static final Logger LOGGER = Logger.getLogger(DefaultLoadTransactionsService.class.getName());

    private final TransactionLoader loader;
    private final TransactionRepository repository;

    public DefaultLoadTransactionsService(TransactionLoader loader, TransactionRepository repository) {
        this.loader = loader;
        this.repository = repository;
    }

    @Override
    public void loadAndPersist() {
        var transactions = this.loader.load();
        LOGGER.log(Level.INFO, "{0} transactions loaded from csv files", transactions.size());
        this.repository.save(transactions);
    }
}
