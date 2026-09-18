package com.slms.model;

import java.util.Objects;

/**
 * Abstract base class for every actor in the system.
 * Demonstrates abstraction + inheritance: concrete behaviour
 * (dashboard title, permission description) is supplied by subclasses.
 */
public abstract class User {

    private String userId;
    private String name;
    private String username;
    private String passwordHash;
    private final Role role;

    protected User(String userId, String name, String username, String passwordHash, Role role) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public abstract String getDashboardTitle();

    public abstract String getPermissionSummary();

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }

    public void setName(String name) { this.name = name; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    /**
     * Serializes the common fields to a CSV row. Subclasses append their own
     * extra fields via {@link #extraCsvFields()}.
     */
    public final String toCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append(userId).append(',')
          .append(name).append(',')
          .append(username).append(',')
          .append(passwordHash).append(',')
          .append(role.name());
        String extra = extraCsvFields();
        if (extra != null && !extra.isEmpty()) {
            sb.append(',').append(extra);
        }
        return sb.toString();
    }

    /** Hook for subclasses to add extra CSV columns. Empty by default. */
    protected String extraCsvFields() {
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
