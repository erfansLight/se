package edu.example.library.repo;

import edu.example.library.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByUserId(Long userId);
    boolean existsByStudentNumber(String studentNumber);
}
