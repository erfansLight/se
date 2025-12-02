package edu.example.library;
import edu.example.library.service.*;
import edu.example.library.model.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final AuthService auth = new AuthService();
    private static final BookService books = new BookService();
    private static final BorrowService borrow = new BorrowService(books, auth);
    private static final ReportService report = new ReportService(borrow, books, auth);

    public static void main(String[] args){
        seedDefaultData();
        System.out.println("Default sample data loaded. Use CLI menus to interact.");
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.println("\n--- Library CLI ---");
            System.out.println("1) Guest\n2) Student\n3) Employee\n4) Admin\n0) Exit");
            System.out.print("Choice: ");
            String c = sc.nextLine().trim();
            switch(c){
                case "1": guestMenu(sc); break;
                case "2": studentTopMenu(sc); break;
                case "3": employeeMenu(sc); break;
                case "4": adminMenu(sc); break;
                case "0": System.out.println("Bye"); return;
                default: System.out.println("Invalid");
            }
        }
    }

    private static void seedDefaultData(){
        // Books (5)
        books.seedBook(new Book("B001","Clean Code","Robert C. Martin",2008));
        books.seedBook(new Book("B002","Design Patterns","GoF",1994));
        books.seedBook(new Book("B003","Introduction to Algorithms","Cormen",2009));
        books.seedBook(new Book("B004","Operating System Concepts","Silberschatz",2018));
        books.seedBook(new Book("B005","Database System Concepts","Silberschatz",2019));
        // Students (3) - active
        auth.seedUser(new User("ali","1234", UserRole.STUDENT));
        auth.seedUser(new User("sara","1234", UserRole.STUDENT));
        auth.seedUser(new User("reza","1234", UserRole.STUDENT));
        // Employee (1)
        auth.seedUser(new User("emp1","1234", UserRole.EMPLOYEE));
        // Admin already present (admin/admin)

        // Borrow Requests: 9001 APPROVED (ali -> Clean Code), 9002 PENDING (sara -> Design Patterns)
        BorrowRequest r1 = new BorrowRequest("R9001","ali","B001", LocalDate.of(2024,10,1), LocalDate.of(2024,10,7));
        r1.setStatus(RequestStatus.APPROVED);
        borrow.seedRequest(r1);
        // mark book B001 as BORROWED because approved
        books.setStatus("B001", BookStatus.BORROWED);

        BorrowRequest r2 = new BorrowRequest("R9002","sara","B002", LocalDate.of(2024,10,2), LocalDate.of(2024,10,10));
        r2.setStatus(RequestStatus.PENDING);
        borrow.seedRequest(r2);
    }

    private static void guestMenu(Scanner sc){
        System.out.println("Guest: 1) Count Students 2) Search by title 3) Stats 0) Back");
        String c = sc.nextLine().trim();
        switch(c){
            case "1": System.out.println("Registered students: "+auth.allUsers().stream().filter(u->u.getRole()==UserRole.STUDENT).count()); break;
            case "2": System.out.print("Title: "); String t=sc.nextLine(); books.searchByTitleOnly(t).forEach(System.out::println); break;
            case "3": System.out.println("Total students: "+auth.allUsers().size()+" Total books: "+books.allBooks().size()+" Total requests: "+borrow.allRequests().size()); break;
            default: return;
        }
    }

    private static void studentTopMenu(Scanner sc){
        System.out.println("Student: 1) Register 2) Login 0) Back");
        String c = sc.nextLine().trim();
        switch(c){
            case "1": System.out.print("Choose username: "); String nu=sc.nextLine(); System.out.print("Choose password: "); String np=sc.nextLine();
                boolean ok = auth.register(nu,np); System.out.println(ok?"Registered":"Registration failed (maybe duplicate)"); break;
            case "2": studentMenu(sc); break;
            default: return;
        }
    }

    private static void studentMenu(Scanner sc){
        System.out.print("username: "); String u = sc.nextLine();
        System.out.print("password: "); String p = sc.nextLine();
        if(!auth.login(u,p)){ System.out.println("Login failed"); return;}
        System.out.println("Welcome " + u);
        System.out.println("1) Search\n2) Request borrow\n0) Back");
        String c = sc.nextLine().trim();
        switch(c){
            case "1": System.out.print("Title (or blank): "); String t=sc.nextLine();
                System.out.print("Year (or blank): "); String y=sc.nextLine();
                System.out.print("Author (or blank): "); String a=sc.nextLine();
                Integer yi = y.isBlank()?null:Integer.valueOf(y);
                books.search(t.isBlank()?null:t, yi, a.isBlank()?null:a).forEach(System.out::println);
                break;
            case "2": System.out.print("Book id: "); String bid=sc.nextLine();
                System.out.print("From YYYY-MM-DD: "); String f=sc.nextLine();
                System.out.print("To YYYY-MM-DD: "); String to=sc.nextLine();
                try{
                    BorrowRequest r = borrow.createRequest(u, bid, LocalDate.parse(f), LocalDate.parse(to));
                    System.out.println("Request created: " + r.getId());
                }catch(Exception e){ System.out.println("Error: " + e.getMessage());}
                break;
            default: return;
        }
    }

    private static void employeeMenu(Scanner sc){
        System.out.print("username: "); String u = sc.nextLine();
        System.out.print("password: "); String p = sc.nextLine();
        if(!auth.login(u,p)){ System.out.println("Login failed"); return;}
        User uu = auth.getUser(u);
        if(uu.getRole()!=UserRole.EMPLOYEE){ System.out.println("Not an employee"); return;}
        System.out.println("1) Change password 2) Add book 3) Search/edit book 4) Approve todays requests 5) Student history 6) Activate/Deactivate student 7) Receive book 0) Back");
        String c = sc.nextLine().trim();
        switch(c){
            case "1": System.out.print("New password: "); String np=sc.nextLine(); uu.setPassword(np); System.out.println("Done"); break;
            case "2": System.out.print("Title: "); String t=sc.nextLine(); System.out.print("Author: "); String a=sc.nextLine(); System.out.print("Year: "); int y=Integer.parseInt(sc.nextLine()); Book b = books.addBook(t,a,y); System.out.println("Added " + b); break;
            case "3": System.out.print("Search title: "); String st=sc.nextLine(); List<Book> res=books.searchByTitleOnly(st); res.forEach(System.out::println); System.out.print("Book id to edit (or blank): "); String bid=sc.nextLine(); if(!bid.isBlank()){ System.out.print("New title: "); String nt=sc.nextLine(); System.out.print("New author: "); String na=sc.nextLine(); System.out.print("New year: "); int ny=Integer.parseInt(sc.nextLine()); books.updateBook(bid, nt, na, ny); System.out.println("Updated"); } break;
            case "4": // approve requests with from == today or yesterday
                var today = java.time.LocalDate.now();
                borrow.allRequests().stream().filter(r-> (r.getFrom().equals(today) || r.getFrom().equals(today.minusDays(1))) && r.getStatus()==RequestStatus.PENDING).forEach(r->{
                    try{ borrow.approve(r.getId()); System.out.println("Approved " + r.getId()); }catch(Exception e){ System.out.println("Err " + e.getMessage());}
                });
                break;
            case "5": System.out.print("Student username: "); String su=sc.nextLine(); var rep = report.studentReport(su); System.out.println("Total: "+rep.getTotalBorrows()+" NotReturned: "+rep.getNotReturned()+" Delayed: "+rep.getDelayed()); break;
            case "6": System.out.print("Student username: "); String su2=sc.nextLine(); var user=auth.getUser(su2); if(user!=null){ user.setActive(!user.isActive()); System.out.println("Now active="+user.isActive()); } else System.out.println("Not found"); break;
            case "7": System.out.print("Request id: "); String rid=sc.nextLine(); System.out.print("Received date YYYY-MM-DD: "); String rd=sc.nextLine(); try{ borrow.receive(rid, LocalDate.parse(rd)); System.out.println("Received"); }catch(Exception e){ System.out.println("Err: " + e.getMessage()); } break;
            default: return;
        }
    }

    private static void adminMenu(Scanner sc){
        System.out.print("username: "); String u = sc.nextLine();
        System.out.print("password: "); String p = sc.nextLine();
        if(!auth.login(u,p)){ System.out.println("Login failed"); return;}
        User uu = auth.getUser(u);
        if(uu.getRole()!=UserRole.ADMIN){ System.out.println("Not admin"); return;}
        System.out.println("1) Create employee 2) View employee performance 3) Library stats 0) Back");
        String c = sc.nextLine().trim();
        switch(c){
            case "1": System.out.print("emp username: "); String eu=sc.nextLine(); System.out.print("emp pass: "); String ep=sc.nextLine(); auth.addEmployee(eu, ep); System.out.println("Created"); break;
            case "2": System.out.println("Employee list:"); auth.allUsers().stream().filter(x->x.getRole()==UserRole.EMPLOYEE).forEach(x-> System.out.println(x.getUsername())); break;
            case "3": var s = report.libraryStats(); System.out.println("Total reqs: "+s.getTotalRequests()+" Approved: "+s.getTotalApproved()+" AvgDays: "+s.getAvgDays()); break;
            default: return;
        }
    }
}
