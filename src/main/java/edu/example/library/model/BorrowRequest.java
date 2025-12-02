package edu.example.library.model;
import java.time.LocalDate;
public class BorrowRequest {
    private final String id;
    private final String studentUsername;
    private final String bookId;
    private final LocalDate from;
    private final LocalDate to;
    private RequestStatus status = RequestStatus.PENDING;
    private LocalDate receivedAt = null;
    public BorrowRequest(String id, String studentUsername, String bookId, LocalDate from, LocalDate to){
        this.id=id; this.studentUsername=studentUsername; this.bookId=bookId; this.from=from; this.to=to;
    }
    public String getId(){return id;}
    public String getStudentUsername(){return studentUsername;}
    public String getBookId(){return bookId;}
    public LocalDate getFrom(){return from;}
    public LocalDate getTo(){return to;}
    public RequestStatus getStatus(){return status;}
    public void setStatus(RequestStatus s){this.status = s;}
    public LocalDate getReceivedAt(){return receivedAt;}
    public void setReceivedAt(LocalDate d){this.receivedAt = d;}
    @Override public String toString(){ return id+" | "+studentUsername+" | "+bookId+" | "+status+" | from:"+from+" to:"+to; }
}
