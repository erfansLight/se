package org.example.app.users;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    // Dummy class to test abstract User
    static class TestUser extends User {
        public TestUser(String username, String password, String name) {
            super(username, password, name);
        }

        @Override
        public void showMenu() {
            System.out.println("Test Menu");
        }
    }

    @Test
    void testConstructorAndGetters() {
        User user = new TestUser("user1", "pass123", "John Doe");

        assertEquals("user1", user.getUsername());
        assertEquals("John Doe", user.getName());
    }

    @Test
    void testPasswordCheck() {
        User user = new TestUser("u", "secret", "N");

        assertTrue(user.checkPassword("secret"));
        assertFalse(user.checkPassword("wrong"));
    }

    @Test
    void testChangePassword() {
        User user = new TestUser("u", "old", "N");

        assertTrue(user.checkPassword("old"));

        user.changePassword("new123");

        assertTrue(user.checkPassword("new123"));
        assertFalse(user.checkPassword("old"));
    }

    @Test
    void testShowMenuOutput() {
        User user = new TestUser("u", "p", "N");

        // Capture output
        var out = new java.io.ByteArrayOutputStream();
        var original = System.out;
        System.setOut(new java.io.PrintStream(out));

        user.showMenu();

        System.setOut(original);

        assertTrue(out.toString().contains("Test Menu"));
    }
}
