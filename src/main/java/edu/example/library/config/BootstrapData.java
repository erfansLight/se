package edu.example.library.config;

import edu.example.library.entity.*;
import edu.example.library.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BootstrapData {

    @Bean
    public CommandLineRunner seed(UserAccountRepository userRepo,
                                  StudentRepository studentRepo,
                                  EmployeeRepository employeeRepo,
                                  BookRepository bookRepo,
                                  PasswordEncoder passwordEncoder) {
        return args -> {
            // Create default admin if missing
            userRepo.findByUsername("admin").orElseGet(() -> {
                UserAccount admin = new UserAccount("admin", passwordEncoder.encode("admin123"), Role.ADMIN);
                return userRepo.save(admin);
            });

            // Create a sample employee if missing
            userRepo.findByUsername("employee").orElseGet(() -> {
                UserAccount empUser = new UserAccount("employee", passwordEncoder.encode("employee123"), Role.EMPLOYEE);
                empUser = userRepo.save(empUser);
                employeeRepo.save(new EmployeeProfile(empUser, "Default Employee"));
                return empUser;
            });

            // Seed books (idempotent-ish: only if empty)
            if (bookRepo.count() == 0) {
                bookRepo.save(new Book("Effective Java", "Joshua Bloch", 2018, "9780134685991", 3));
                bookRepo.save(new Book("Clean Code", "Robert C. Martin", 2008, "9780132350884", 2));
                bookRepo.save(new Book("Design Patterns", "Erich Gamma", 1994, "9780201633610", 1));
            }
        };
    }
}
