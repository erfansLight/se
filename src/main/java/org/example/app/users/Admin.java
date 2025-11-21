package org.example.app.users;

public class Admin extends User {
    public Admin(String username, String password, String name) {
        super(username, password, name);
    }

    @Override
    public void showMenu() {
        System.out.println("\n=== منوی مدیر سیستم ===");
        System.out.println("1. تعریف کارمند جدید");
        System.out.println("2. مشاهده عملکرد کارمندان");
        System.out.println("3. مشاهده آمار امانت‌ها");
        System.out.println("4. مشاهده آمار دانشجویان");
        System.out.println("5. خروج");
    }
}
