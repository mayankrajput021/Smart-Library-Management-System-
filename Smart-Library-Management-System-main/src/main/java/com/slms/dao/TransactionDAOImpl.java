package com.slms.dao;

import com.slms.model.Transaction;
import com.slms.model.TransactionStatus;
import com.slms.util.FileManager;
import com.slms.util.IdGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** CSV-file backed implementation of {@link TransactionDAO}. */
public class TransactionDAOImpl implements TransactionDAO {

    private static final String FILE = "transactions.csv";
    private static final String HEADER = "transactionId,bookId,userId,issueDate,dueDate,returnDate,fineAmount,status";
    private final FileManager fileManager = FileManager.getInstance();

    public TransactionDAOImpl() {
        fileManager.ensureFile(FILE, HEADER);
    }

    private Transaction parse(String row) {
        String[] p = row.split(",", -1);
        LocalDate returnDate = p[5].isEmpty() ? null : LocalDate.parse(p[5]);
        return new Transaction(p[0], p[1], p[2], LocalDate.parse(p[3]), LocalDate.parse(p[4]),
                returnDate, Double.parseDouble(p[6]), TransactionStatus.valueOf(p[7]));
    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> list = new ArrayList<>();
        for (String row : fileManager.readLines(FILE)) {
            list.add(parse(row));
        }
        return list;
    }

    @Override
    public Optional<Transaction> findById(String transactionId) {
        return findAll().stream().filter(t -> t.getTransactionId().equals(transactionId)).findFirst();
    }

    @Override
    public List<Transaction> findByUser(String userId) {
        List<Transaction> list = new ArrayList<>();
        for (Transaction t : findAll()) if (t.getUserId().equals(userId)) list.add(t);
        return list;
    }

    @Override
    public List<Transaction> findByBook(String bookId) {
        List<Transaction> list = new ArrayList<>();
        for (Transaction t : findAll()) if (t.getBookId().equals(bookId)) list.add(t);
        return list;
    }

    @Override
    public List<Transaction> findActiveByUserAndBook(String userId, String bookId) {
        List<Transaction> list = new ArrayList<>();
        for (Transaction t : findAll()) {
            if (t.getUserId().equals(userId) && t.getBookId().equals(bookId)
                    && t.getStatus() != TransactionStatus.RETURNED) {
                list.add(t);
            }
        }
        return list;
    }

    @Override
    public String add(Transaction transaction) {
        List<String> existing = fileManager.readLines(FILE);
        String id = IdGenerator.nextId("T", existing);
        Transaction withId = new Transaction(id, transaction.getBookId(), transaction.getUserId(),
                transaction.getIssueDate(), transaction.getDueDate(), transaction.getReturnDate(),
                transaction.getFineAmount(), transaction.getStatus());
        fileManager.appendLine(FILE, withId.toCsv());
        return id;
    }

    @Override
    public boolean update(Transaction transaction) {
        List<String> rows = fileManager.readLines(FILE);
        boolean found = false;
        List<String> updated = new ArrayList<>();
        for (String row : rows) {
            if (row.startsWith(transaction.getTransactionId() + ",")) {
                updated.add(transaction.toCsv());
                found = true;
            } else {
                updated.add(row);
            }
        }
        if (found) fileManager.writeAll(FILE, HEADER, updated);
        return found;
    }
}
