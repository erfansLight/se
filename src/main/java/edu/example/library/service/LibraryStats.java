package edu.example.library.service;
public class LibraryStats {
    private final int totalRequests;
    private final int totalApproved;
    private final double avgDays;
    public LibraryStats(int totalRequests, int totalApproved, double avgDays){
        this.totalRequests=totalRequests; this.totalApproved=totalApproved; this.avgDays=avgDays;
    }
    public int getTotalRequests(){return totalRequests;}
    public int getTotalApproved(){return totalApproved;}
    public double getAvgDays(){return avgDays;}
}
