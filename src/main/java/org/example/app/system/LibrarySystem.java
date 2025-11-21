package org.example.app.system;

import org.example.app.models.Book;
import org.example.app.models.Loan;
import org.example.app.models.LoanRequest;
import org.example.app.models.LoanStatus;
import org.example.app.users.Admin;
import org.example.app.users.Employee;
import org.example.app.users.Guest;
import org.example.app.users.Student;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class LibrarySystem {
    private final Map<String, Student> students = new HashMap<>();
    private final Map<String, Employee> employees = new HashMap<>();
    private final Map<String, Admin> admins = new HashMap<>();
    private final Map<Integer, Book> books = new HashMap<>();
    private final List<LoanRequest> requests = new ArrayList<>();
    private final List<Loan> loans = new ArrayList<>();

    private final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        LibrarySystem sys = new LibrarySystem();
        sys.seedDemoData();
        sys.showMainMenu();
    }

    private void seedDemoData() {
        // یک مدیر اولیه
        Admin admin = new Admin("admin", "admin", "مدیر اصلی");
        admins.put(admin.getUsername(), admin);

        // یک کارمند اولیه
        Employee emp = new Employee("emp1", "1234", "کارمند اول");
        employees.put(emp.getUsername(), emp);

        // چند دانشجو
        Student s1 = new Student("sara", "1111", "سارا");
        Student s2 = new Student("ali", "2222", "علی");
        students.put(s1.getUsername(), s1);
        students.put(s2.getUsername(), s2);

        // چند کتاب
        addBook(new Book("ساخت و توسعه نرم‌افزار", "نویسنده الف", 2010));
        addBook(new Book("طراحی الگوریتم‌ها", "نویسنده ب", 2015));
        addBook(new Book("سیستم‌عامل‌ها", "نویسنده ج", 2008));
    }

    private void addBook(Book book) {
        books.put(book.getId(), book);
    }

    public void showMainMenu() {
        while (true) {
            System.out.println("\n=== سیستم مدیریت کتابخانه دانشگاه ===");
            System.out.println("1. ورود دانشجو");
            System.out.println("2. ورود کارمند");
            System.out.println("3. ورود مدیر");
            System.out.println("4. منوی مهمان");
            System.out.println("5. ثبت نام دانشجو");
            System.out.println("6. خروج");
            System.out.print("انتخاب کنید: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> studentLogin();
                case "2" -> employeeLogin();
                case "3" -> adminLogin();
                case "4" -> guestMenu();
                case "5" -> registerStudent();
                case "6" -> { System.out.println("خروج..."); return; }
                default -> System.out.println("گزینه نامعتبر");
            }
        }
    }

    // ---------- org.example.app.users.Guest ----------
    private void guestMenu() {
        Guest guest = new Guest();
        while (true) {
            guest.showMenu();
            System.out.print("انتخاب: ");
            String c = sc.nextLine().trim();
            switch (c) {
                case "1" -> System.out.println("تعداد دانشجویان ثبت‌شده: " + students.size());
                case "2" -> {
                    System.out.print("عنوان کتاب (یا بخشی از عنوان): ");
                    String q = sc.nextLine().trim();
                    List<Book> res = searchBooksByTitle(q);
                    if (res.isEmpty()) System.out.println("نتیجه‌ای یافت نشد.");
                    else res.forEach(b -> System.out.println(b));
                }
                case "3" -> showSimpleStats();
                case "4" -> { return; }
                default -> System.out.println("نامعتبر");
            }
        }
    }

    private void showSimpleStats() {
        long currentlyLoaned = books.values().stream().filter(b -> !b.isAvailable()).count();
        System.out.println("تعداد کل دانشجویان: " + students.size());
        System.out.println("تعداد کل کتاب‌ها: " + books.size());
        System.out.println("تعداد کل امانت‌ها (تاریخچه): " + loans.size());
        System.out.println("تعداد کتاب‌هایی که در حال حاضر امانت داده شده‌اند: " + currentlyLoaned);
    }

    // ---------- org.example.app.users.Student ----------
    private void registerStudent() {
        System.out.println("\n=== ثبت نام دانشجو ===");
        System.out.print("نام کاربری: ");
        String user = sc.nextLine().trim();
        if (students.containsKey(user)) { System.out.println("نام کاربری از قبل وجود دارد."); return; }
        System.out.print("رمز عبور: ");
        String pass = sc.nextLine().trim();
        System.out.print("نام: ");
        String name = sc.nextLine().trim();
        Student s = new Student(user, pass, name);
        students.put(user, s);
        System.out.println("ثبت نام با موفقیت انجام شد.");
    }

    private void studentLogin() {
        System.out.print("نام کاربری: ");
        String user = sc.nextLine().trim();
        System.out.print("رمز عبور: ");
        String pass = sc.nextLine().trim();
        Student s = students.get(user);
        if (s == null || !s.checkPassword(pass)) { System.out.println("ورود نامعتبر"); return; }
        if (!s.isActive()) { System.out.println("کاربر غیرفعال است. امکان امانت وجود ندارد."); }
        else studentMenu(s);
    }

    private void studentMenu(Student s) {
        while (true) {
            s.showMenu();
            System.out.print("انتخاب: ");
            String c = sc.nextLine().trim();
            switch (c) {
                case "1" -> {
                    System.out.print("جستجو (عنوان یا نویسنده یا سال): ");
                    String q = sc.nextLine().trim();
                    List<Book> res = searchBooksFlexible(q);
                    if (res.isEmpty()) System.out.println("نتیجه‌ای یافت نشد.");
                    else res.forEach(b -> System.out.println(b));
                }
                case "2" -> {
                    System.out.print("شماره کتاب برای درخواست امانت: ");
                    int id = Integer.parseInt(sc.nextLine().trim());
                    Book book = books.get(id);
                    if (book == null) { System.out.println("کتابی با این شماره یافت نشد."); break; }
                    if (!book.isAvailable()) { System.out.println("این کتاب در حال حاضر امانت داده شده است."); break; }
                    try {
                        System.out.print("تاریخ شروع (YYYY-MM-DD): ");
                        LocalDate start = LocalDate.parse(sc.nextLine().trim());
                        System.out.print("تاریخ پایان (YYYY-MM-DD): ");
                        LocalDate end = LocalDate.parse(sc.nextLine().trim());
                        if (end.isBefore(start)) { System.out.println("تاریخ پایان نمی‌تواند قبل از شروع باشد."); break; }
                        LoanRequest req = new LoanRequest(s, book, start, end);
                        requests.add(req);
                        System.out.println("درخواست شما ثبت شد. منتظر تایید کارمند باشید. (ID=" + req.getId() + ")");
                    } catch (Exception ex) {
                        System.out.println("فرمت تاریخ نامعتبر است.");
                    }
                }
                case "3" -> {
                    if (s.getLoanHistory().isEmpty()) System.out.println("هیچ وامی ثبت نشده.");
                    else s.getLoanHistory().forEach(l -> System.out.println(l));
                }
                case "4" -> {
                    System.out.print("رمز جدید: ");
                    String np = sc.nextLine().trim();
                    s.changePassword(np);
                    System.out.println("رمز با موفقیت تغییر کرد.");
                }
                case "5" -> { return; }
                default -> System.out.println("نامعتبر");
            }
        }
    }

    private List<Book> searchBooksByTitle(String q) {
        String low = q.toLowerCase();
        return books.values().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(low))
                .collect(Collectors.toList());
    }

    private List<Book> searchBooksFlexible(String q) {
        String low = q.toLowerCase();
        List<Book> byTitle = books.values().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(low))
                .collect(Collectors.toList());
        List<Book> byAuthor = books.values().stream()
                .filter(b -> b.getAuthor().toLowerCase().contains(low))
                .collect(Collectors.toList());
        List<Book> byYear = new ArrayList<>();
        try {
            int y = Integer.parseInt(q);
            byYear = books.values().stream().filter(b -> b.getYear() == y).collect(Collectors.toList());
        } catch (NumberFormatException ignored) {}
        Set<Book> set = new LinkedHashSet<>();
        set.addAll(byTitle); set.addAll(byAuthor); set.addAll(byYear);
        return new ArrayList<>(set);
    }

    // ---------- org.example.app.users.Employee ----------
    private void employeeLogin() {
        System.out.print("نام کاربری کارمند: ");
        String user = sc.nextLine().trim();
        System.out.print("رمز عبور: ");
        String pass = sc.nextLine().trim();
        Employee e = employees.get(user);
        if (e == null || !e.checkPassword(pass)) { System.out.println("ورود نامعتبر"); return; }
        employeeMenu(e);
    }

    private void employeeMenu(Employee e) {
        while (true) {
            e.showMenu();
            System.out.print("انتخاب: ");
            String c = sc.nextLine().trim();
            switch (c) {
                case "1" -> {
                    System.out.print("عنوان: ");
                    String t = sc.nextLine().trim();
                    System.out.print("نویسنده: ");
                    String a = sc.nextLine().trim();
                    System.out.print("سال نشر: ");
                    int y = Integer.parseInt(sc.nextLine().trim());
                    Book b = new Book(t, a, y);
                    addBook(b);
                    System.out.println("کتاب با شناسه " + b.getId() + " ثبت شد.");
                }
                case "2" -> {
                    System.out.print("جستجو (عنوان): ");
                    String q = sc.nextLine().trim();
                    List<Book> res = searchBooksByTitle(q);
                    if (res.isEmpty()) { System.out.println("یافت نشد"); break; }
                    res.forEach(System.out::println);
                    System.out.print("آیا می‌خواهید کتابی را ویرایش کنید؟ (y/n): ");
                    String ans = sc.nextLine().trim();
                    if (ans.equalsIgnoreCase("y")) {
                        System.out.print("شماره کتاب برای ویرایش: ");
                        int id = Integer.parseInt(sc.nextLine().trim());
                        Book book = books.get(id);
                        if (book == null) { System.out.println("کتاب یافت نشد"); break; }
                        System.out.print("عنوان جدید (خالی برای حفظ): ");
                        String nt = sc.nextLine(); if (!nt.isBlank()) book.setTitle(nt.trim());
                        System.out.print("نویسنده جدید (خالی برای حفظ): ");
                        String na = sc.nextLine(); if (!na.isBlank()) book.setAuthor(na.trim());
                        System.out.print("سال جدید (خالی برای حفظ): ");
                        String ny = sc.nextLine(); if (!ny.isBlank()) book.setYear(Integer.parseInt(ny.trim()));
                        System.out.println("به‌روزرسانی انجام شد.");
                    }
                }
                case "3" -> {
                    // نمایش درخواست‌هایی که وضعیت pending و شروع امروز یا دیروز است
                    LocalDate today = LocalDate.now();
                    List<LoanRequest> pending = requests.stream()
                            .filter(r -> r.getStatus() == LoanStatus.PENDING)
                            .collect(Collectors.toList());
                    if (pending.isEmpty()) { System.out.println("درخواستی موجود نیست."); break; }
                    pending.forEach(r -> System.out.println(r));
                    System.out.print("آیا می‌خواهید درخواستی را تایید/رد کنید؟ (y/n): ");
                    String yn = sc.nextLine().trim();
                    if (!yn.equalsIgnoreCase("y")) break;
                    System.out.print("شماره درخواست: ");
                    int id = Integer.parseInt(sc.nextLine().trim());
                    LoanRequest target = requests.stream().filter(r -> r.getId() == id).findFirst().orElse(null);
                    if (target == null) { System.out.println("درخواست یافت نشد."); break; }
                    // فقط اگر شروع درخواست امروز یا دیروز باشد
                    LocalDate start = target.getStartDate();
                    if (!(start.equals(today) || start.equals(today.minusDays(1)))) {
                        System.out.println("این درخواست قابل تایید نیست (شروع باید امروز یا دیروز باشد)."); break;
                    }
                    System.out.print("تایید یا رد؟ (a=تایید / r=رد): ");
                    String ar = sc.nextLine().trim();
                    if (ar.equalsIgnoreCase("a")) {
                        target.approve(e);
                        // ساخت org.example.app.models.Loan
                        Loan loan = new Loan(target.getStudent(), target.getBook(), target.getStartDate());
                        loans.add(loan);
                        target.getStudent().addLoan(loan);
                        System.out.println("درخواست تایید شد و امانت ثبت گردید (LoanID=" + loan.getId() + ")");
                    } else if (ar.equalsIgnoreCase("r")) {
                        target.reject();
                        System.out.println("درخواست رد شد.");
                    }
                }
                case "4" -> {
                    System.out.print("نام کاربری دانشجو برای مشاهده گزارش: ");
                    String u = sc.nextLine().trim();
                    Student st = students.get(u);
                    if (st == null) { System.out.println("یافت نشد"); break; }
                    List<Loan> hist = st.getLoanHistory();
                    System.out.println("تعداد کل امانت‌ها: " + hist.size());
                    long notReturned = hist.stream().filter(l -> !l.isReturned()).count();
                    System.out.println("تعداد کتاب‌های تحویل داده نشده: " + notReturned);
                    long delayed = hist.stream().filter(l -> l.isReturned() && l.getReturnDate().isAfter(l.getBorrowDate().plusDays(0))).count();
                    // توجه: معیار تاخیر ساده است؛ می‌توان دقیق‌تر محاسبه کرد
                    System.out.println("تعداد امانت‌هایی که با تاخیر برگشته‌اند: " + delayed);
                    if (hist.isEmpty()) System.out.println("هیچ سابقه‌ای وجود ندارد."); else hist.forEach(System.out::println);
                }
                case "5" -> {
                    System.out.print("نام کاربری دانشجو برای فعال/غیرفعال: ");
                    String u = sc.nextLine().trim();
                    Student st = students.get(u);
                    if (st == null) { System.out.println("یافت نشد"); break; }
                    st.setActive(!st.isActive());
                    System.out.println("اکنون وضعیت: " + (st.isActive() ? "فعال" : "غیرفعال"));
                }
                case "6" -> {
                    System.out.print("شماره وام برای ثبت بازگشت کتاب: ");
                    int id = Integer.parseInt(sc.nextLine().trim());
                    Loan loan = loans.stream().filter(l -> l.getId() == id).findFirst().orElse(null);
                    if (loan == null) { System.out.println("یافت نشد"); break; }
                    if (loan.isReturned()) { System.out.println("این کتاب قبلاً بازگردانده شده."); break; }
                    loan.setReturnDate(LocalDate.now());
                    System.out.println("بازگشت ثبت شد.");
                }
                case "7" -> {
                    System.out.print("رمز جدید: ");
                    String np = sc.nextLine().trim();
                    e.changePassword(np);
                    System.out.println("رمز با موفقیت تغییر کرد.");
                }
                case "8" -> { return; }
                default -> System.out.println("نامعتبر");
            }
        }
    }

    // ---------- org.example.app.users.Admin ----------
    private void adminLogin() {
        System.out.print("نام کاربری مدیر: ");
        String user = sc.nextLine().trim();
        System.out.print("رمز عبور: ");
        String pass = sc.nextLine().trim();
        Admin a = admins.get(user);
        if (a == null || !a.checkPassword(pass)) { System.out.println("ورود نامعتبر"); return; }
        adminMenu(a);
    }

    private void adminMenu(Admin a) {
        while (true) {
            a.showMenu();
            System.out.print("انتخاب: ");
            String c = sc.nextLine().trim();
            switch (c) {
                case "1" -> {
                    System.out.print("نام کاربری کارمند: ");
                    String u = sc.nextLine().trim();
                    if (employees.containsKey(u)) { System.out.println("وجود دارد"); break; }
                    System.out.print("رمز عبور: ");
                    String p = sc.nextLine().trim();
                    System.out.print("نام: ");
                    String n = sc.nextLine().trim();
                    Employee emp = new Employee(u, p, n);
                    employees.put(u, emp);
                    System.out.println("کارمند ثبت شد.");
                }
                case "2" -> {
                    System.out.println("عملکرد کارمندان:");
                    for (Employee emp : employees.values()) {
                        long registered = books.values().stream().filter(b -> false).count(); // placeholder
                        // چون در این نسخه اطلاعات ثبت‌کننده کتاب نگهداری نمی‌شود، این آمار ساده است
                        long given = loans.stream().filter(l -> l.getBorrowDate() != null).count();
                        long received = loans.stream().filter(Loan::isReturned).count();
                        System.out.println(emp.getUsername() + " - منتشر ثبت شده: (N/A) - امانت داده شده: " + given + " - تحویل گرفته: " + received);
                    }
                }
                case "3" -> {
                    System.out.println("آمار امانت‌ها:");
                    long totalRequests = requests.size();
                    long totalGiven = loans.size();
                    double avgDays = loans.stream()
                            .filter(Loan::isReturned)
                            .mapToLong(l -> java.time.temporal.ChronoUnit.DAYS.between(l.getBorrowDate(), l.getReturnDate()))
                            .average().orElse(0.0);
                    System.out.println("تعداد درخواست‌ها: " + totalRequests);
                    System.out.println("تعداد کل امانت‌ها: " + totalGiven);
                    System.out.println("میانگین روزهای امانت (برای وام‌های برگشتی): " + String.format("%.2f", avgDays));
                }
                case "4" -> {
                    System.out.println("آمار دانشجویان:");
                    for (Student s : students.values()) {
                        List<Loan> hist = s.getLoanHistory();
                        long notReturned = hist.stream().filter(l -> !l.isReturned()).count();
                        long delayed = hist.stream().filter(l -> l.isReturned() && l.getReturnDate().isAfter(l.getBorrowDate())).count();
                        System.out.println(s.getUsername() + " - کل امانت‌ها: " + hist.size() + " - تحویل نشده: " + notReturned + " - تاخیرها: " + delayed);
                    }
                    // 10 دانشجوی با بیشترین تاخیر
                    List<Map.Entry<String, Long>> delays = students.values().stream()
                            .collect(Collectors.toMap(Student::getUsername,
                                    st -> st.getLoanHistory().stream().filter(l -> l.isReturned() && l.getReturnDate().isAfter(l.getBorrowDate())).count()))
                            .entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                            .limit(10).collect(Collectors.toList());
                    System.out.println("10 دانشجوی با بیشترین تاخیر:");
                    delays.forEach(e -> System.out.println(e.getKey() + " - " + e.getValue()));
                }
                case "5" -> { return; }
                default -> System.out.println("نامعتبر");
            }
        }
    }
}
