package com.slms.model;

import java.util.Objects;

/** Represents a single title in the library catalog (may have multiple copies). */
public class Book {

    private String bookId;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private int totalCopies;
    private int availableCopies;

    public Book(String bookId, String title, String author, String isbn,
                String category, int totalCopies, int availableCopies) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    public String getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public String getCategory() { return category; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }

    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setCategory(String category) { this.category = category; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }

    public boolean isAvailable() { return availableCopies > 0; }

    public void decrementAvailable() { this.availableCopies = Math.max(0, this.availableCopies - 1); }

    public void incrementAvailable() {
        this.availableCopies = Math.min(this.totalCopies, this.availableCopies + 1);
    }

    public String toCsv() {
        return String.join(",", bookId, escape(title), escape(author), isbn,
                escape(category), String.valueOf(totalCopies), String.valueOf(availableCopies));
    }

    private String escape(String value) {
        // Guard against commas breaking the naive CSV format used by this project.
        return value == null ? "" : value.replace(",", ";");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return Objects.equals(bookId, book.bookId);
    }

    @Override
    public int hashCode() { return Objects.hash(bookId); }

    @Override
    public String toString() {
        return String.format("[%s] %s by %s (%s) - %d/%d available",
                bookId, title, author, category, availableCopies, totalCopies);
    }
}
