package edu.example.library.service;

import edu.example.library.dto.EmployeeCreateRequest;
import edu.example.library.entity.EmployeeProfile;
import edu.example.library.entity.Role;
import edu.example.library.entity.UserAccount;
import edu.example.library.exception.ApiException;
import edu.example.library.repo.EmployeeRepository;
import edu.example.library.repo.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminEmployeeService {

    private final UserAccountRepository userRepo;
    private final EmployeeRepository employeeRepo;
    private final PasswordEncoder passwordEncoder;

    public AdminEmployeeService(UserAccountRepository userRepo, EmployeeRepository employeeRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.employeeRepo = employeeRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public EmployeeProfile createEmployee(EmployeeCreateRequest req) {
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new ApiException(HttpStatus.CONFLICT, "USERNAME_TAKEN", "Username is already taken");
        }
        UserAccount user = new UserAccount(req.getUsername(), passwordEncoder.encode(req.getPassword()), Role.EMPLOYEE);
        user = userRepo.save(user);
        EmployeeProfile emp = new EmployeeProfile(user, req.getFullName());
        return employeeRepo.save(emp);
    }

    public List<EmployeeProfile> listEmployees() {
        return employeeRepo.findAll();
    }
}
