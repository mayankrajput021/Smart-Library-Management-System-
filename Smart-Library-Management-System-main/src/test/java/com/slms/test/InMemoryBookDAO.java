package com.slms.test;

import com.slms.dao.BookDAO;
import com.slms.model.Book;

import java.util.*;

/** In-memory fake used only by the test suite, so tests never touch disk. */
public class InMemoryBookDAO implements BookDAO {
    private final Map<String, Book> store = new LinkedHashMap<>();
    private int counter = 0;

    @Override public List<Book> findAll() { return new ArrayList<>(store.values()); }

    @Override public Optional<Book> findById(String bookId) { return Optional.ofNullable(store.get(bookId)); }

    @Override public Optional<Book> findByIsbn(String isbn) {
        return store.values().stream().filter(b -> b.getIsbn().equals(isbn)).findFirst();
    }

    @Override public List<Book> search(String keyword) {
        List<Book> out = new ArrayList<>();
        for (Book b : store.values()) {
            if (b.getTitle().toLowerCase().contains(keyword.toLowerCase())) out.add(b);
        }
        return out;
    }

    @Override public String add(Book book) {
        String id = "B" + String.format("%03d", ++counter);
        store.put(id, new Book(id, book.getTitle(), book.getAuthor(), book.getIsbn(),
                book.getCategory(), book.getTotalCopies(), book.getAvailableCopies()));
        return id;
    }

    @Override public boolean update(Book book) {
        if (!store.containsKey(book.getBookId())) return false;
        store.put(book.getBookId(), book);
        return true;
    }

    @Override public boolean delete(String bookId) { return store.remove(bookId) != null; }
}
