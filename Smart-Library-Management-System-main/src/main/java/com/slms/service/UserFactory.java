package com.slms.service;

import com.slms.exception.InvalidInputException;
import com.slms.model.Admin;
import com.slms.model.Librarian;
import com.slms.model.Member;
import com.slms.model.Role;
import com.slms.model.User;

/**
 * Factory design pattern: centralizes creation of the correct {@link User}
 * subtype so calling code never needs to know the concrete classes.
 */
public final class UserFactory {

    private UserFactory() { }

    public static User createUser(Role role, String tempId, String name, String username, String passwordHash)
            throws InvalidInputException {
        if (role == null) {
            throw new InvalidInputException("Role must be specified.");
        }
        switch (role) {
            case ADMIN:
                return new Admin(tempId, name, username, passwordHash);
            case LIBRARIAN:
                return new Librarian(tempId, name, username, passwordHash);
            case MEMBER:
                return new Member(tempId, name, username, passwordHash);
            default:
                throw new InvalidInputException("Unsupported role: " + role);
        }
    }
}
