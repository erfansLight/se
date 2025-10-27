import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    private boolean active = true;
    private final List<Loan> loanHistory = new ArrayList<>();

    public Student(String username, String password, String name) {
        super(username, password, name);
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public List<Loan> getLoanHistory() { return loanHistory; }
    public void addLoan(Loan loan) { loanHistory.add(loan); }

    @Override
    public void showMenu() {
        System.out.println("\n=== منوی دانشجو ===");
        System.out.println("1. جستجوی کتاب (عنوان/نویسنده/سال)");
        System.out.println("2. ثبت درخواست امانت کتاب");
        System.out.println("3. مشاهده تاریخچه امانت‌ها");
        System.out.println("4. تغییر رمز عبور");
        System.out.println("5. خروج");
    }
}
