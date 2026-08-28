package com.example.demo.domain.service;

import com.example.demo.domain.model.Transaction;

import java.util.List;

public interface TransactionLoader {
    List<Transaction> load();
}
