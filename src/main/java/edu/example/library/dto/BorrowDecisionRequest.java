package edu.example.library.dto;

import jakarta.validation.constraints.Size;

public class BorrowDecisionRequest {
    @Size(max = 500)
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
