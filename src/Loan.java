import java.time.LocalDate;

public class Loan {
    private static int counter = 1;
    private final int id;
    private final Student student;
    private final Book book;
    private final LocalDate borrowDate;
    private LocalDate returnDate = null;

    public Loan(Student student, Book book, LocalDate borrowDate) {
        this.id = counter++;
        this.student = student;
        this.book = book;
        this.borrowDate = borrowDate;
    }

    public int getId() { return id; }
    public Student getStudent() { return student; }
    public Book getBook() { return book; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate date) {
        this.returnDate = date;
        book.setAvailable(true);
    }

    public boolean isReturned() { return returnDate != null; }

    @Override
    public String toString() {
        return String.format("وام %d: %s -> %s از %s تا %s",
                id, student.getUsername(), book.getTitle(), borrowDate, returnDate == null ? "نامشخص" : returnDate.toString());
    }
}
