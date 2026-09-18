package com.slms.model;

/** Librarian: manages the book catalog and issue/return transactions. */
public class Librarian extends User {

    public Librarian(String userId, String name, String username, String passwordHash) {
        super(userId, name, username, passwordHash, Role.LIBRARIAN);
    }

    @Override
    public String getDashboardTitle() {
        return "Librarian Dashboard";
    }

    @Override
    public String getPermissionSummary() {
        return "Manage catalog, issue/return books, view operational reports";
    }
}
