package com.slms.dao;

import com.slms.model.Book;
import java.util.List;
import java.util.Optional;

/** Data-access contract for Book persistence (DAO design pattern). */
public interface BookDAO {
    List<Book> findAll();
    Optional<Book> findById(String bookId);
    Optional<Book> findByIsbn(String isbn);
    List<Book> search(String keyword);
    String add(Book book); // returns generated id
    boolean update(Book book);
    boolean delete(String bookId);
}
