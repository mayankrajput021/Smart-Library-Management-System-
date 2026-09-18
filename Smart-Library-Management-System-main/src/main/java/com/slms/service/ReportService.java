package com.slms.service;

import com.slms.model.Book;
import com.slms.model.Transaction;
import com.slms.model.TransactionStatus;
import com.slms.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/** Analytics/reporting logic built on top of the other services (Module 4). */
public class ReportService {

    private final BookService bookService;
    private final TransactionService transactionService;
    private final UserService userService;

    public ReportService(BookService bookService, TransactionService transactionService, UserService userService) {
        this.bookService = bookService;
        this.transactionService = transactionService;
        this.userService = userService;
    }

    /** Top N most-borrowed books, ranked by number of issue transactions. */
    public List<Map.Entry<Book, Long>> mostBorrowedBooks(int topN) {
        List<Transaction> all = transactionService.listAll();
        Map<String, Long> counts = all.stream()
                .collect(Collectors.groupingBy(Transaction::getBookId, Collectors.counting()));

        List<Map.Entry<Book, Long>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : counts.entrySet()) {
            bookService.listAll().stream()
                    .filter(b -> b.getBookId().equals(entry.getKey()))
                    .findFirst()
                    .ifPresent(book -> result.add(new AbstractMap.SimpleEntry<>(book, entry.getValue())));
        }
        result.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));
        return result.size() > topN ? result.subList(0, topN) : result;
    }

    /** All currently overdue (or already-late) transactions. */
    public List<Transaction> overdueReport() {
        LocalDate today = LocalDate.now();
        List<Transaction> overdue = new ArrayList<>();
        for (Transaction t : transactionService.listAll()) {
            boolean stillOut = t.getStatus() != TransactionStatus.RETURNED;
            if (stillOut && t.getDueDate().isBefore(today)) {
                overdue.add(t);
            }
        }
        return overdue;
    }

    /** Borrowing activity + total fines paid for one member. */
    public String memberActivityReport(String userId) {
        List<Transaction> history = transactionService.historyForUser(userId);
        double totalFines = history.stream().mapToDouble(Transaction::getFineAmount).sum();
        long active = history.stream().filter(t -> t.getStatus() != TransactionStatus.RETURNED).count();
        return String.format("User %s: %d total loans, %d currently active, total fines paid: %.2f",
                userId, history.size(), active, totalFines);
    }

    /** Overall catalog health: total titles, total copies, copies currently on loan. */
    public String inventorySummary() {
        List<Book> books = bookService.listAll();
        int totalTitles = books.size();
        int totalCopies = books.stream().mapToInt(Book::getTotalCopies).sum();
        int available = books.stream().mapToInt(Book::getAvailableCopies).sum();
        return String.format("Titles: %d | Total copies: %d | Available: %d | On loan: %d",
                totalTitles, totalCopies, available, totalCopies - available);
    }
}
