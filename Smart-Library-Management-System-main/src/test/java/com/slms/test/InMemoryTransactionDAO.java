package com.slms.test;

import com.slms.dao.TransactionDAO;
import com.slms.model.Transaction;
import com.slms.model.TransactionStatus;

import java.util.*;

/** In-memory fake used only by the test suite, so tests never touch disk. */
public class InMemoryTransactionDAO implements TransactionDAO {
    private final Map<String, Transaction> store = new LinkedHashMap<>();
    private int counter = 0;

    @Override public List<Transaction> findAll() { return new ArrayList<>(store.values()); }

    @Override public Optional<Transaction> findById(String transactionId) {
        return Optional.ofNullable(store.get(transactionId));
    }

    @Override public List<Transaction> findByUser(String userId) {
        List<Transaction> out = new ArrayList<>();
        for (Transaction t : store.values()) if (t.getUserId().equals(userId)) out.add(t);
        return out;
    }

    @Override public List<Transaction> findByBook(String bookId) {
        List<Transaction> out = new ArrayList<>();
        for (Transaction t : store.values()) if (t.getBookId().equals(bookId)) out.add(t);
        return out;
    }

    @Override public List<Transaction> findActiveByUserAndBook(String userId, String bookId) {
        List<Transaction> out = new ArrayList<>();
        for (Transaction t : store.values()) {
            if (t.getUserId().equals(userId) && t.getBookId().equals(bookId)
                    && t.getStatus() != TransactionStatus.RETURNED) out.add(t);
        }
        return out;
    }

    @Override public String add(Transaction transaction) {
        String id = "T" + String.format("%03d", ++counter);
        Transaction rebuilt = new Transaction(id, transaction.getBookId(), transaction.getUserId(),
                transaction.getIssueDate(), transaction.getDueDate(), transaction.getReturnDate(),
                transaction.getFineAmount(), transaction.getStatus());
        store.put(id, rebuilt);
        return id;
    }

    @Override public boolean update(Transaction transaction) {
        if (!store.containsKey(transaction.getTransactionId())) return false;
        store.put(transaction.getTransactionId(), transaction);
        return true;
    }
}
