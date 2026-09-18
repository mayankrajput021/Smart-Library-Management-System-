package com.slms.test;

import com.slms.model.Role;
import com.slms.model.User;
import com.slms.service.UserService;

/** Registration and authentication tests for UserService, run against an in-memory DAO. */
public class UserServiceTest {

    public static void run() {
        System.out.println("UserServiceTest:");
        UserService service = new UserService(new InMemoryUserDAO());

        try {
            User u = service.register("Alice Member", "alice", "secret123", Role.MEMBER);
            Assert.assertEquals("role assigned correctly", Role.MEMBER, u.getRole());

            Assert.assertThrows("rejects duplicate username",
                    () -> service.register("Alice Clone", "alice", "otherpass", Role.MEMBER));

            User loggedIn = service.login("alice", "secret123");
            Assert.assertEquals("login returns matching user id", u.getUserId(), loggedIn.getUserId());

            Assert.assertThrows("rejects wrong password", () -> service.login("alice", "wrongpass"));
            Assert.assertThrows("rejects unknown username", () -> service.login("nobody", "whatever"));

            Assert.assertTrue("password is never stored in plaintext",
                    !u.getPasswordHash().equals("secret123"));
        } catch (Exception e) {
            Assert.assertTrue("unexpected exception in happy-path flow: " + e.getMessage(), false);
        }
    }
}
