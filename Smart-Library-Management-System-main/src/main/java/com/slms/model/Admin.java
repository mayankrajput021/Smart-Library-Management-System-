package com.slms.model;

/** System administrator: full access, including user management and reports. */
public class Admin extends User {

    public Admin(String userId, String name, String username, String passwordHash) {
        super(userId, name, username, passwordHash, Role.ADMIN);
    }

    @Override
    public String getDashboardTitle() {
        return "Administrator Dashboard";
    }

    @Override
    public String getPermissionSummary() {
        return "Manage users, manage catalog, manage transactions, view all reports";
    }
}
