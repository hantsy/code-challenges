package com.example.demo;

import java.io.IOException;
import java.util.List;

interface TransactionLoader {
    List<Transaction> load() throws IOException;
}

/*
class FileTransactionLoader implements com.example.demo.TransactionLoader{

    private final File file;

    public FileTransactionLoader(File file) {
        this.file = file;
    }

    @Override
    public List<com.example.demo.Transaction> load() throws IOException {
        return null;
    }
}*/