package com.slms.test;

import com.slms.dao.UserDAO;
import com.slms.model.*;

import java.util.*;

/** In-memory fake used only by the test suite, so tests never touch disk. */
public class InMemoryUserDAO implements UserDAO {
    private final Map<String, User> store = new LinkedHashMap<>();
    private int counter = 0;

    @Override public List<User> findAll() { return new ArrayList<>(store.values()); }

    @Override public Optional<User> findById(String userId) { return Optional.ofNullable(store.get(userId)); }

    @Override public Optional<User> findByUsername(String username) {
        return store.values().stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst();
    }

    @Override public String add(User user) {
        String id = "U" + String.format("%03d", ++counter);
        User rebuilt;
        switch (user.getRole()) {
            case ADMIN: rebuilt = new Admin(id, user.getName(), user.getUsername(), user.getPasswordHash()); break;
            case LIBRARIAN: rebuilt = new Librarian(id, user.getName(), user.getUsername(), user.getPasswordHash()); break;
            default: rebuilt = new Member(id, user.getName(), user.getUsername(), user.getPasswordHash());
        }
        store.put(id, rebuilt);
        return id;
    }

    @Override public boolean update(User user) {
        if (!store.containsKey(user.getUserId())) return false;
        store.put(user.getUserId(), user);
        return true;
    }

    @Override public boolean delete(String userId) { return store.remove(userId) != null; }
}
