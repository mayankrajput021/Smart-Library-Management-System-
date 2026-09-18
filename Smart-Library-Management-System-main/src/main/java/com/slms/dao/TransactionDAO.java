package com.slms.dao;

import com.slms.model.Transaction;
import java.util.List;
import java.util.Optional;

/** Data-access contract for Transaction (issue/return) persistence. */
public interface TransactionDAO {
    List<Transaction> findAll();
    Optional<Transaction> findById(String transactionId);
    List<Transaction> findByUser(String userId);
    List<Transaction> findByBook(String bookId);
    List<Transaction> findActiveByUserAndBook(String userId, String bookId);
    String add(Transaction transaction);
    boolean update(Transaction transaction);
}
