package edu.example.library.repo;

import edu.example.library.entity.BorrowRequest;
import edu.example.library.entity.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BorrowRequestRepository extends JpaRepository<BorrowRequest, Long> {

    List<BorrowRequest> findByStatus(BorrowStatus status);

    List<BorrowRequest> findByStudentIdOrderByRequestedAtDesc(Long studentId);

    long countByStatus(BorrowStatus status);

    List<BorrowRequest> findByStatusAndDueDateBefore(BorrowStatus status, LocalDate dueDate);
}
