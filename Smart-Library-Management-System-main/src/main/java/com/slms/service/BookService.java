package com.slms.service;

import com.slms.dao.BookDAO;
import com.slms.dao.BookDAOImpl;
import com.slms.exception.BookNotFoundException;
import com.slms.exception.DuplicateEntryException;
import com.slms.exception.InvalidInputException;
import com.slms.model.Book;
import com.slms.util.AppLogger;
import com.slms.util.ValidationUtil;

import java.util.List;

/** Business logic for catalog / inventory management (Module 2). */
public class BookService {

    private final BookDAO bookDAO;
    private final AppLogger logger = AppLogger.getInstance();

    public BookService() { this(new BookDAOImpl()); }

    public BookService(BookDAO bookDAO) { this.bookDAO = bookDAO; }

    public Book addBook(String title, String author, String isbn, String category, int totalCopies)
            throws InvalidInputException, DuplicateEntryException {
        ValidationUtil.requireNonEmpty(title, "Title");
        ValidationUtil.requireNonEmpty(author, "Author");
        ValidationUtil.requireValidIsbn(isbn);
        ValidationUtil.requireNonEmpty(category, "Category");
        ValidationUtil.requirePositive(totalCopies, "Total copies");

        if (bookDAO.findByIsbn(isbn).isPresent()) {
            throw new DuplicateEntryException("A book with ISBN " + isbn + " already exists.");
        }
        Book draft = new Book("TEMP", title, author, isbn, category, totalCopies, totalCopies);
        String id = bookDAO.add(draft);
        logger.info("Added book '" + title + "' (" + id + "), copies=" + totalCopies);
        return bookDAO.findById(id).orElseThrow(() -> new IllegalStateException("Newly added book vanished"));
    }

    public List<Book> listAll() { return bookDAO.findAll(); }

    public List<Book> search(String keyword) { return bookDAO.search(keyword); }

    public Book getById(String bookId) throws BookNotFoundException {
        return bookDAO.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("No book with id " + bookId));
    }

    public void updateBook(String bookId, String title, String author, String category, int totalCopies)
            throws BookNotFoundException, InvalidInputException {
        ValidationUtil.requireNonEmpty(title, "Title");
        ValidationUtil.requireNonEmpty(author, "Author");
        ValidationUtil.requirePositive(totalCopies, "Total copies");

        Book existing = getById(bookId);
        int borrowedCopies = existing.getTotalCopies() - existing.getAvailableCopies();
        if (totalCopies < borrowedCopies) {
            throw new InvalidInputException(
                    "Cannot set total copies below currently borrowed copies (" + borrowedCopies + ").");
        }
        existing.setTitle(title);
        existing.setAuthor(author);
        existing.setCategory(category);
        existing.setTotalCopies(totalCopies);
        existing.setAvailableCopies(totalCopies - borrowedCopies);
        bookDAO.update(existing);
        logger.info("Updated book " + bookId);
    }

    public void removeBook(String bookId) throws BookNotFoundException {
        Book existing = getById(bookId);
        if (existing.getAvailableCopies() != existing.getTotalCopies()) {
            logger.warn("Removing book " + bookId + " while copies are still on loan.");
        }
        bookDAO.delete(bookId);
        logger.info("Removed book " + bookId);
    }
}
