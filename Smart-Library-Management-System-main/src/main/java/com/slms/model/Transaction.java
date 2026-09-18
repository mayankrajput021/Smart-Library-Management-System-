package com.slms.model;

import java.time.LocalDate;

/** Represents one book issue event, and its eventual return + fine. */
public class Transaction {

    public static final int LOAN_PERIOD_DAYS = 14;
    public static final double FINE_PER_DAY = 5.0; // currency units per day overdue

    private String transactionId;
    private String bookId;
    private String userId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate; // null while book is still out
    private double fineAmount;
    private TransactionStatus status;

    public Transaction(String transactionId, String bookId, String userId,
                        LocalDate issueDate, LocalDate dueDate, LocalDate returnDate,
                        double fineAmount, TransactionStatus status) {
        this.transactionId = transactionId;
        this.bookId = bookId;
        this.userId = userId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fineAmount = fineAmount;
        this.status = status;
    }

    public String getTransactionId() { return transactionId; }
    public String getBookId() { return bookId; }
    public String getUserId() { return userId; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public double getFineAmount() { return fineAmount; }
    public TransactionStatus getStatus() { return status; }

    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    public String toCsv() {
        return String.join(",",
                transactionId, bookId, userId,
                issueDate.toString(), dueDate.toString(),
                returnDate == null ? "" : returnDate.toString(),
                String.valueOf(fineAmount), status.name());
    }

    @Override
    public String toString() {
        return String.format("Txn[%s] book=%s user=%s issued=%s due=%s returned=%s fine=%.2f status=%s",
                transactionId, bookId, userId, issueDate, dueDate,
                returnDate == null ? "-" : returnDate, fineAmount, status);
    }
}
