package org.example.app.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee("emp1", "1234", "Employee One");
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals("emp1", employee.getUsername());
        assertEquals("Employee One", employee.getName());
        assertTrue(employee.checkPassword("1234"));
    }

    @Test
    void testChangePassword() {
        employee.changePassword("newPass");
        assertTrue(employee.checkPassword("newPass"));
        assertFalse(employee.checkPassword("1234"));
    }

    @Test
    void testShowMenuPrintsCorrectOutput() {
        // Capture output printed by showMenu()
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(output));

        employee.showMenu();

        System.setOut(original); // Restore original output

        String text = output.toString();

        assertTrue(text.contains("=== منوی کارمند کتابخانه ==="));
        assertTrue(text.contains("1. ثبت کتاب جدید"));
        assertTrue(text.contains("2. جستجو و ویرایش اطلاعات کتاب"));
        assertTrue(text.contains("3. بررسی و تایید درخواست‌های امانت"));
        assertTrue(text.contains("4. مشاهده گزارش تاریخچه یک دانشجو"));
        assertTrue(text.contains("5. فعال/غیرفعال کردن دانشجو"));
        assertTrue(text.contains("6. ثبت دریافت کتاب (ثبت برگردان)"));
        assertTrue(text.contains("7. تغییر رمز عبور"));
        assertTrue(text.contains("8. خروج"));
    }
}
