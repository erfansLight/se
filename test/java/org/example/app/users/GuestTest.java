package org.example.app.users;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class GuestTest {

    @Test
    void testShowMenuOutput() {
        Guest guest = new Guest();

        // Capture printed output
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(output));

        guest.showMenu();

        System.setOut(original); // Restore original output

        String text = output.toString();

        assertTrue(text.contains("=== منوی مهمان ==="));
        assertTrue(text.contains("1. مشاهده تعداد دانشجویان ثبت‌شده"));
        assertTrue(text.contains("2. جستجوی کتاب (فقط بر اساس عنوان)"));
        assertTrue(text.contains("3. مشاهده آمار کلی"));
        assertTrue(text.contains("4. خروج"));
    }
}
