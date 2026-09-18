package com.slms.dao;

import com.slms.model.Admin;
import com.slms.model.Librarian;
import com.slms.model.Member;
import com.slms.model.Role;
import com.slms.model.User;
import com.slms.util.FileManager;
import com.slms.util.IdGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CSV-file backed implementation of {@link UserDAO}.
 * Reconstructs the correct polymorphic subtype (Admin/Librarian/Member)
 * from the stored role column when reading rows back.
 */
public class UserDAOImpl implements UserDAO {

    private static final String FILE = "users.csv";
    private static final String HEADER = "userId,name,username,passwordHash,role,extra";
    private final FileManager fileManager = FileManager.getInstance();

    public UserDAOImpl() {
        fileManager.ensureFile(FILE, HEADER);
    }

    private User parse(String row) {
        String[] p = row.split(",", -1);
        String userId = p[0], name = p[1], username = p[2], passwordHash = p[3];
        Role role = Role.valueOf(p[4]);
        switch (role) {
            case ADMIN:
                return new Admin(userId, name, username, passwordHash);
            case LIBRARIAN:
                return new Librarian(userId, name, username, passwordHash);
            case MEMBER:
                double fine = (p.length > 5 && !p[5].isEmpty()) ? Double.parseDouble(p[5]) : 0.0;
                return new Member(userId, name, username, passwordHash, fine);
            default:
                throw new IllegalStateException("Unknown role: " + role);
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        for (String row : fileManager.readLines(FILE)) {
            users.add(parse(row));
        }
        return users;
    }

    @Override
    public Optional<User> findById(String userId) {
        return findAll().stream().filter(u -> u.getUserId().equals(userId)).findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return findAll().stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst();
    }

    @Override
    public String add(User user) {
        List<String> existing = fileManager.readLines(FILE);
        String id = IdGenerator.nextId("U", existing);
        User withId = rebuildWithId(user, id);
        fileManager.appendLine(FILE, withId.toCsv());
        return id;
    }

    private User rebuildWithId(User user, String id) {
        switch (user.getRole()) {
            case ADMIN:
                return new Admin(id, user.getName(), user.getUsername(), user.getPasswordHash());
            case LIBRARIAN:
                return new Librarian(id, user.getName(), user.getUsername(), user.getPasswordHash());
            case MEMBER:
                return new Member(id, user.getName(), user.getUsername(), user.getPasswordHash());
            default:
                throw new IllegalStateException("Unknown role: " + user.getRole());
        }
    }

    @Override
    public boolean update(User user) {
        List<String> rows = fileManager.readLines(FILE);
        boolean found = false;
        List<String> updated = new ArrayList<>();
        for (String row : rows) {
            if (row.startsWith(user.getUserId() + ",")) {
                updated.add(user.toCsv());
                found = true;
            } else {
                updated.add(row);
            }
        }
        if (found) fileManager.writeAll(FILE, HEADER, updated);
        return found;
    }

    @Override
    public boolean delete(String userId) {
        List<String> rows = fileManager.readLines(FILE);
        List<String> remaining = new ArrayList<>();
        boolean found = false;
        for (String row : rows) {
            if (row.startsWith(userId + ",")) {
                found = true;
            } else {
                remaining.add(row);
            }
        }
        if (found) fileManager.writeAll(FILE, HEADER, remaining);
        return found;
    }
}
