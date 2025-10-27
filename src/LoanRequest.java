import java.time.LocalDate;

public class LoanRequest {
    private static int counter = 1;
    private final int id;
    private final Student student;
    private final Book book;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private LoanStatus status = LoanStatus.PENDING;
    private Employee approvedBy = null;

    public LoanRequest(Student student, Book book, LocalDate startDate, LocalDate endDate) {
        this.id = counter++;
        this.student = student;
        this.book = book;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() { return id; }
    public Student getStudent() { return student; }
    public Book getBook() { return book; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public LoanStatus getStatus() { return status; }
    public void approve(Employee emp) {
        this.status = LoanStatus.APPROVED;
        this.approvedBy = emp;
        book.setAvailable(false);
    }
    public void reject() { this.status = LoanStatus.REJECTED; }

    @Override
    public String toString() {
        return String.format("درخواست %d: دانشجو=%s, کتاب=%s, از=%s تا=%s, وضعیت=%s",
                id, student.getUsername(), book.getTitle(), startDate, endDate, status);
    }
}
