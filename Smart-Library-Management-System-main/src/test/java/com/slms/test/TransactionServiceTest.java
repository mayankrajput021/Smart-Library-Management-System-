package com.slms.test;

import com.slms.model.Book;
import com.slms.model.Transaction;
import com.slms.service.BookService;
import com.slms.service.TransactionService;

/** Issue/return and fine-calculation tests for TransactionService. */
public class TransactionServiceTest {

    public static void run() {
        System.out.println("TransactionServiceTest:");
        InMemoryBookDAO bookDAO = new InMemoryBookDAO();
        BookService bookService = new BookService(bookDAO);
        TransactionService txnService = new TransactionService(new InMemoryTransactionDAO(), bookDAO);

        try {
            Book book = bookService.addBook("The Pragmatic Programmer", "Hunt & Thomas",
                    "9780135957059", "Software", 1);

            Transaction issued = txnService.issueBook("U001", book.getBookId());
            Assert.assertEquals("due date is 14 days after issue",
                    issued.getIssueDate().plusDays(14), issued.getDueDate());

            Book afterIssue = bookService.getById(book.getBookId());
            Assert.assertEquals("available copies decrease after issue", 0, afterIssue.getAvailableCopies());

            Assert.assertThrows("cannot issue the only remaining copy twice",
                    () -> txnService.issueBook("U002", book.getBookId()));

            Transaction returned = txnService.returnBook(issued.getTransactionId());
            Assert.assertEquals("no fine when returned on/before due date", 0.0, returned.getFineAmount());

            Book afterReturn = bookService.getById(book.getBookId());
            Assert.assertEquals("available copies restored after return", 1, afterReturn.getAvailableCopies());

            Assert.assertThrows("cannot return the same transaction twice",
                    () -> txnService.returnBook(issued.getTransactionId()));
        } catch (Exception e) {
            Assert.assertTrue("unexpected exception in happy-path flow: " + e.getMessage(), false);
        }
    }
}
