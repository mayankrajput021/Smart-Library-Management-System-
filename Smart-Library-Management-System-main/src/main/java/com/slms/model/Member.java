package com.slms.model;

/** Library member: can browse the catalog and borrow/return books. */
public class Member extends User {

    private double outstandingFine;

    public Member(String userId, String name, String username, String passwordHash) {
        this(userId, name, username, passwordHash, 0.0);
    }

    public Member(String userId, String name, String username, String passwordHash, double outstandingFine) {
        super(userId, name, username, passwordHash, Role.MEMBER);
        this.outstandingFine = outstandingFine;
    }

    public double getOutstandingFine() { return outstandingFine; }

    public void addFine(double amount) { this.outstandingFine += amount; }

    public void clearFine() { this.outstandingFine = 0.0; }

    @Override
    public String getDashboardTitle() {
        return "Member Dashboard";
    }

    @Override
    public String getPermissionSummary() {
        return "Browse catalog, borrow/return books, view personal history";
    }

    @Override
    protected String extraCsvFields() {
        return String.valueOf(outstandingFine);
    }
}
