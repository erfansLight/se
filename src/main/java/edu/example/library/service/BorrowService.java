package edu.example.library.service;

import edu.example.library.exception.*;
import edu.example.library.model.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BorrowService {
    private final Map<String, BorrowRequest> requests = new LinkedHashMap<>();
    private int counter = 1;
    private final BookService bookService;
    private final AuthService authService;
    public BorrowService(BookService bs, AuthService as){ this.bookService = bs; this.authService = as; }
    public BorrowRequest createRequest(String studentUsername, String bookId, LocalDate from, LocalDate to){
        User u = authService.getUser(studentUsername);
        if(u==null) throw new InvalidStudentStatusException("Student does not exist");
        if(!u.isActive()) throw new InvalidStudentStatusException("Student inactive");
        Book b = bookService.getById(bookId);
        if(b==null) throw new BookNotAvailableException("Book not found");
        if(b.getStatus()==BookStatus.BORROWED) throw new BookNotAvailableException("Book already borrowed");
        String id = "R"+(counter++);
        BorrowRequest r = new BorrowRequest(id, studentUsername, bookId, from, to);
        requests.put(id, r);
        return r;
    }
    /**
 * Approve a request using the system date (LocalDate.now()).
 * For deterministic tests or custom flows, use approve(requestId, approvalDate).
 */
public BorrowRequest approve(String requestId){
    return approve(requestId, LocalDate.now());
}

/**
 * Approves only requests whose start date (from) is either the approval date (today)
 * or the day before (yesterday), as required by the scenario.
 */
public BorrowRequest approve(String requestId, LocalDate approvalDate){
    BorrowRequest r = requests.get(requestId);
    if(r==null) throw new InvalidRequestStatusException("Request not found");
    if(r.getStatus()!=RequestStatus.PENDING) throw new InvalidRequestStatusException("Request not pending");

    // rule: only approve if start date is today or yesterday
    if(!(r.getFrom().equals(approvalDate) || r.getFrom().equals(approvalDate.minusDays(1)))){
        throw new InvalidRequestStatusException("Request start date must be today or yesterday");
    }

    Book b = bookService.getById(r.getBookId());
    if(b==null) throw new BookNotAvailableException("Book not found");
    if(b.getStatus()==BookStatus.BORROWED) throw new BookNotAvailableException("Book already borrowed");

    r.setStatus(RequestStatus.APPROVED);
    bookService.setStatus(b.getId(), BookStatus.BORROWED);
    return r;
}

/**
 * Receives (returns) an approved borrowed book and records the receive date.
 * After receiving, the related book becomes AVAILABLE again.
 */
public void receive(String requestId, LocalDate receivedDate){
    BorrowRequest r = requests.get(requestId);
    if(r==null) throw new InvalidRequestStatusException("Request not found");
    if(r.getStatus()!=RequestStatus.APPROVED) throw new InvalidRequestStatusException("Request must be approved first");
    if(r.getReceivedAt()!=null) throw new InvalidRequestStatusException("Book already received");
    r.setReceivedAt(receivedDate);

    Book b = bookService.getById(r.getBookId());
    if(b!=null){
        bookService.setStatus(b.getId(), BookStatus.AVAILABLE);
    }
}

public Collection<BorrowRequest> allRequests(){ return requests.values(); }
    public List<BorrowRequest> forStudent(String studentUsername){
        return requests.values().stream().filter(r->r.getStudentUsername().equals(studentUsername)).collect(Collectors.toList());
    }
    public void seedRequest(BorrowRequest r){
        requests.put(r.getId(), r);
    }
}
