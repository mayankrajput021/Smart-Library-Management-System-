package com.slms.service;

import com.slms.dao.UserDAO;
import com.slms.dao.UserDAOImpl;
import com.slms.exception.AuthenticationException;
import com.slms.exception.DuplicateEntryException;
import com.slms.exception.InvalidInputException;
import com.slms.exception.UserNotFoundException;
import com.slms.model.Role;
import com.slms.model.User;
import com.slms.util.AppLogger;
import com.slms.util.ValidationUtil;

import java.util.List;
import java.util.Optional;

/** Business logic for user registration, authentication and management (Module 1). */
public class UserService {

    private final UserDAO userDAO;
    private final AppLogger logger = AppLogger.getInstance();

    public UserService() { this(new UserDAOImpl()); }

    public UserService(UserDAO userDAO) { this.userDAO = userDAO; }

    public User register(String name, String username, String plainPassword, Role role)
            throws InvalidInputException, DuplicateEntryException {
        ValidationUtil.requireNonEmpty(name, "Name");
        ValidationUtil.requireNonEmpty(username, "Username");
        ValidationUtil.requireNonEmpty(plainPassword, "Password");

        if (userDAO.findByUsername(username).isPresent()) {
            throw new DuplicateEntryException("Username '" + username + "' is already taken.");
        }
        String hash = PasswordUtil.hash(plainPassword);
        User draft = UserFactory.createUser(role, "TEMP", name, username, hash);
        String id = userDAO.add(draft);
        logger.info("Registered new " + role + " user: " + username + " (" + id + ")");
        return userDAO.findById(id).orElseThrow(() -> new IllegalStateException("Newly added user vanished"));
    }

    public User login(String username, String plainPassword) throws AuthenticationException {
        Optional<User> found = userDAO.findByUsername(username);
        if (!found.isPresent() || !PasswordUtil.matches(plainPassword, found.get().getPasswordHash())) {
            logger.warn("Failed login attempt for username: " + username);
            throw new AuthenticationException("Invalid username or password.");
        }
        logger.info("User logged in: " + username);
        return found.get();
    }

    public List<User> listAll() { return userDAO.findAll(); }

    public User getById(String userId) throws UserNotFoundException {
        return userDAO.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
    }

    public boolean deleteUser(String userId) throws UserNotFoundException {
        boolean removed = userDAO.delete(userId);
        if (!removed) throw new UserNotFoundException("No user with id " + userId);
        logger.info("Deleted user " + userId);
        return true;
    }
}
