import java.io.IOException;
import java.util.List;

interface TransactionLoader {
    List<Transaction> load() throws IOException;
}

/*
class FileTransactionLoader implements TransactionLoader{

    private final File file;

    public FileTransactionLoader(File file) {
        this.file = file;
    }

    @Override
    public List<Transaction> load() throws IOException {
        return null;
    }
}*/