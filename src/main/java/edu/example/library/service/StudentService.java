package edu.example.library.service;

import edu.example.library.entity.StudentProfile;
import edu.example.library.exception.ApiException;
import edu.example.library.repo.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentService {

    private final StudentRepository studentRepo;

    public StudentService(StudentRepository studentRepo) {
        this.studentRepo = studentRepo;
    }

    public StudentProfile get(Long studentId) {
        return studentRepo.findById(studentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "STUDENT_NOT_FOUND", "Student not found"));
    }

    @Transactional
    public StudentProfile setActive(Long studentId, boolean active) {
        StudentProfile s = get(studentId);
        s.setActive(active);
        return studentRepo.save(s);
    }
}
