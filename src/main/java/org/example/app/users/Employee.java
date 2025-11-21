package org.example.app.users;

public class Employee extends User {
    public Employee(String username, String password, String name) {
        super(username, password, name);
    }

    @Override
    public void showMenu() {
        System.out.println("\n=== منوی کارمند کتابخانه ===");
        System.out.println("1. ثبت کتاب جدید");
        System.out.println("2. جستجو و ویرایش اطلاعات کتاب");
        System.out.println("3. بررسی و تایید درخواست‌های امانت");
        System.out.println("4. مشاهده گزارش تاریخچه یک دانشجو");
        System.out.println("5. فعال/غیرفعال کردن دانشجو");
        System.out.println("6. ثبت دریافت کتاب (ثبت برگردان)");
        System.out.println("7. تغییر رمز عبور");
        System.out.println("8. خروج");
    }
}
