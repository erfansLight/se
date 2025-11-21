package org.example.app.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class AdminTest {

    private Admin admin;

    @BeforeEach
    void setUp() {
        admin = new Admin("adminUser", "1234", "Admin Name");
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals("adminUser", admin.getUsername());
        assertTrue(admin.checkPassword("1234"));
        assertEquals("Admin Name", admin.getName());
    }

    @Test
    void testChangePassword() {
        admin.changePassword("newPass");
        assertTrue(admin.checkPassword("newPass"));
        assertFalse(admin.checkPassword("1234"));
    }

    @Test
    void testShowMenuOutputsCorrectly() {
        // Capture printed output of showMenu()
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        admin.showMenu();

        System.setOut(original); // restore console output

        String output = out.toString();

        assertTrue(output.contains("=== منوی مدیر سیستم ==="));
        assertTrue(output.contains("1. تعریف کارمند جدید"));
        assertTrue(output.contains("2. مشاهده عملکرد کارمندان"));
        assertTrue(output.contains("3. مشاهده آمار امانت‌ها"));
        assertTrue(output.contains("4. مشاهده آمار دانشجویان"));
        assertTrue(output.contains("5. خروج"));
    }
}
