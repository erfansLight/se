package edu.example.library.service;
public class StudentReport {
    private final String username;
    private final long totalBorrows;
    private final long notReturned;
    private final long delayed;
    public StudentReport(String username, long totalBorrows, long notReturned, long delayed){
        this.username=username; this.totalBorrows=totalBorrows; this.notReturned=notReturned; this.delayed=delayed;
    }
    public String getUsername(){return username;}
    public long getTotalBorrows(){return totalBorrows;}
    public long getNotReturned(){return notReturned;}
    public long getDelayed(){return delayed;}
}
