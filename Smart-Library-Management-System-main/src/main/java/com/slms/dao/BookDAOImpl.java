package com.slms.dao;

import com.slms.model.Book;
import com.slms.util.FileManager;
import com.slms.util.IdGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** CSV-file backed implementation of {@link BookDAO}. */
public class BookDAOImpl implements BookDAO {

    private static final String FILE = "books.csv";
    private static final String HEADER = "bookId,title,author,isbn,category,totalCopies,availableCopies";
    private final FileManager fileManager = FileManager.getInstance();

    public BookDAOImpl() {
        fileManager.ensureFile(FILE, HEADER);
    }

    private Book parse(String row) {
        String[] p = row.split(",", -1);
        return new Book(p[0], p[1], p[2], p[3], p[4],
                Integer.parseInt(p[5]), Integer.parseInt(p[6]));
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        for (String row : fileManager.readLines(FILE)) {
            books.add(parse(row));
        }
        return books;
    }

    @Override
    public Optional<Book> findById(String bookId) {
        return findAll().stream().filter(b -> b.getBookId().equals(bookId)).findFirst();
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return findAll().stream().filter(b -> b.getIsbn().equals(isbn)).findFirst();
    }

    @Override
    public List<Book> search(String keyword) {
        String lower = keyword.toLowerCase();
        List<Book> matches = new ArrayList<>();
        for (Book b : findAll()) {
            if (b.getTitle().toLowerCase().contains(lower)
                    || b.getAuthor().toLowerCase().contains(lower)
                    || b.getCategory().toLowerCase().contains(lower)) {
                matches.add(b);
            }
        }
        return matches;
    }

    @Override
    public String add(Book book) {
        List<String> existing = fileManager.readLines(FILE);
        String id = IdGenerator.nextId("B", existing);
        Book withId = new Book(id, book.getTitle(), book.getAuthor(), book.getIsbn(),
                book.getCategory(), book.getTotalCopies(), book.getAvailableCopies());
        fileManager.appendLine(FILE, withId.toCsv());
        return id;
    }

    @Override
    public boolean update(Book book) {
        List<String> rows = fileManager.readLines(FILE);
        boolean found = false;
        List<String> updated = new ArrayList<>();
        for (String row : rows) {
            if (row.startsWith(book.getBookId() + ",")) {
                updated.add(book.toCsv());
                found = true;
            } else {
                updated.add(row);
            }
        }
        if (found) fileManager.writeAll(FILE, HEADER, updated);
        return found;
    }

    @Override
    public boolean delete(String bookId) {
        List<String> rows = fileManager.readLines(FILE);
        List<String> remaining = new ArrayList<>();
        boolean found = false;
        for (String row : rows) {
            if (row.startsWith(bookId + ",")) {
                found = true;
            } else {
                remaining.add(row);
            }
        }
        if (found) fileManager.writeAll(FILE, HEADER, remaining);
        return found;
    }
}
