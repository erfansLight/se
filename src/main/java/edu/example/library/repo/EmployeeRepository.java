package edu.example.library.repo;

import edu.example.library.entity.EmployeeProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<EmployeeProfile, Long> {
    Optional<EmployeeProfile> findByUserId(Long userId);
}
