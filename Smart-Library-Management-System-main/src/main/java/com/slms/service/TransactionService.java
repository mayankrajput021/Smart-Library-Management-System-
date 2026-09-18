package com.slms.service;

import com.slms.dao.BookDAO;
import com.slms.dao.BookDAOImpl;
import com.slms.dao.TransactionDAO;
import com.slms.dao.TransactionDAOImpl;
import com.slms.exception.BookNotFoundException;
import com.slms.exception.BookUnavailableException;
import com.slms.exception.InvalidInputException;
import com.slms.model.Book;
import com.slms.model.Transaction;
import com.slms.model.TransactionStatus;
import com.slms.util.AppLogger;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/** Business logic for issuing/returning books and fine calculation (Module 3). */
public class TransactionService {

    private final TransactionDAO transactionDAO;
    private final BookDAO bookDAO;
    private final AppLogger logger = AppLogger.getInstance();

    public TransactionService() { this(new TransactionDAOImpl(), new BookDAOImpl()); }

    public TransactionService(TransactionDAO transactionDAO, BookDAO bookDAO) {
        this.transactionDAO = transactionDAO;
        this.bookDAO = bookDAO;
    }

    public Transaction issueBook(String userId, String bookId)
            throws BookNotFoundException, BookUnavailableException {
        Book book = bookDAO.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("No book with id " + bookId));
        if (!book.isAvailable()) {
            throw new BookUnavailableException("No available copies of '" + book.getTitle() + "'.");
        }
        if (!transactionDAO.findActiveByUserAndBook(userId, bookId).isEmpty()) {
            throw new BookUnavailableException("This user already has an active loan for this book.");
        }
        LocalDate today = LocalDate.now();
        LocalDate due = today.plusDays(Transaction.LOAN_PERIOD_DAYS);
        Transaction draft = new Transaction("TEMP", bookId, userId, today, due, null, 0.0,
                TransactionStatus.ISSUED);
        String id = transactionDAO.add(draft);

        book.decrementAvailable();
        bookDAO.update(book);
        logger.info("Issued book " + bookId + " to user " + userId + " (txn " + id + "), due " + due);
        return transactionDAO.findById(id).orElseThrow(() -> new IllegalStateException("Txn vanished"));
    }

    public Transaction returnBook(String transactionId)
            throws InvalidInputException, BookNotFoundException {
        Transaction txn = transactionDAO.findById(transactionId)
                .orElseThrow(() -> new InvalidInputException("No transaction with id " + transactionId));
        if (txn.getStatus() == TransactionStatus.RETURNED) {
            throw new InvalidInputException("Transaction " + transactionId + " was already returned.");
        }
        LocalDate today = LocalDate.now();
        long overdueDays = Math.max(0, ChronoUnit.DAYS.between(txn.getDueDate(), today));
        double fine = overdueDays * Transaction.FINE_PER_DAY;

        txn.setReturnDate(today);
        txn.setFineAmount(fine);
        txn.setStatus(TransactionStatus.RETURNED);
        transactionDAO.update(txn);

        Book book = bookDAO.findById(txn.getBookId())
                .orElseThrow(() -> new BookNotFoundException("Book " + txn.getBookId() + " no longer exists"));
        book.incrementAvailable();
        bookDAO.update(book);

        logger.info("Returned book " + txn.getBookId() + " (txn " + transactionId + "), fine=" + fine);
        return txn;
    }

    /** Marks and returns any ISSUED transactions whose due date has passed, without closing them. */
    public List<Transaction> refreshOverdueStatuses() {
        LocalDate today = LocalDate.now();
        List<Transaction> all = transactionDAO.findAll();
        for (Transaction t : all) {
            if (t.getStatus() == TransactionStatus.ISSUED && t.getDueDate().isBefore(today)) {
                t.setStatus(TransactionStatus.OVERDUE);
                transactionDAO.update(t);
            }
        }
        return all;
    }

    public List<Transaction> historyForUser(String userId) { return transactionDAO.findByUser(userId); }

    public List<Transaction> historyForBook(String bookId) { return transactionDAO.findByBook(bookId); }

    public List<Transaction> listAll() { return transactionDAO.findAll(); }
}
