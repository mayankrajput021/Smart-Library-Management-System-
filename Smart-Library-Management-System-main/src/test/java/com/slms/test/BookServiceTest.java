package com.slms.test;

import com.slms.model.Book;
import com.slms.model.Role;
import com.slms.service.BookService;

/** Validation and CRUD tests for BookService, run against an in-memory DAO. */
public class BookServiceTest {

    public static void run() {
        System.out.println("BookServiceTest:");
        BookService service = new BookService(new InMemoryBookDAO());

        Assert.assertThrows("rejects empty title",
                () -> service.addBook("", "Author", "1234567890", "Fiction", 3));

        Assert.assertThrows("rejects invalid ISBN",
                () -> service.addBook("Title", "Author", "abc", "Fiction", 3));

        Assert.assertThrows("rejects non-positive copies",
                () -> service.addBook("Title", "Author", "1234567890", "Fiction", 0));

        try {
            Book b = service.addBook("Clean Code", "Robert C. Martin", "9780132350884", "Software", 3);
            Assert.assertEquals("available copies equal total on creation", 3, b.getAvailableCopies());

            Assert.assertThrows("rejects duplicate ISBN",
                    () -> service.addBook("Clean Code 2", "R. Martin", "9780132350884", "Software", 1));

            service.updateBook(b.getBookId(), "Clean Code", "Robert C. Martin", "Software", 5);
            Book updated = service.getById(b.getBookId());
            Assert.assertEquals("update changes total copies", 5, updated.getTotalCopies());

            Assert.assertTrue("search finds book by title", !service.search("clean").isEmpty());

            service.removeBook(b.getBookId());
            Assert.assertThrows("getById fails after removal",
                    () -> service.getById(b.getBookId()));
        } catch (Exception e) {
            Assert.assertTrue("unexpected exception in happy-path flow: " + e.getMessage(), false);
        }
    }
}
