package edu.example.library.service;

import edu.example.library.config.JwtService;
import edu.example.library.config.UserPrincipal;
import edu.example.library.dto.AuthResponse;
import edu.example.library.dto.ChangePasswordRequest;
import edu.example.library.dto.LoginRequest;
import edu.example.library.dto.RegisterStudentRequest;
import edu.example.library.entity.Role;
import edu.example.library.entity.StudentProfile;
import edu.example.library.entity.UserAccount;
import edu.example.library.exception.ApiException;
import edu.example.library.repo.StudentRepository;
import edu.example.library.repo.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserAccountRepository userRepo;
    private final StudentRepository studentRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserAccountRepository userRepo,
                       StudentRepository studentRepo,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepo = userRepo;
        this.studentRepo = studentRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public StudentProfile registerStudent(RegisterStudentRequest req) {
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new ApiException(HttpStatus.CONFLICT, "USERNAME_TAKEN", "Username is already taken");
        }
        if (studentRepo.existsByStudentNumber(req.getStudentNumber())) {
            throw new ApiException(HttpStatus.CONFLICT, "STUDENTNO_TAKEN", "Student number is already registered");
        }

        UserAccount user = new UserAccount(req.getUsername(), passwordEncoder.encode(req.getPassword()), Role.STUDENT);
        user = userRepo.save(user);

        StudentProfile student = new StudentProfile(user, req.getFullName(), req.getStudentNumber());
        return studentRepo.save(student);
    }

    public AuthResponse login(LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        UserAccount user = principal.getUser();
        String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getId(), user.getRole().name());
    }

    @Transactional
    public void changePassword(ChangePasswordRequest req) {
        UserAccount current = currentUser();
        if (!passwordEncoder.matches(req.getOldPassword(), current.getPasswordHash())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "BAD_OLD_PASSWORD", "Old password is incorrect");
        }
        current.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
        userRepo.save(current);
    }

    public UserAccount currentUser() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !(a.getPrincipal() instanceof UserPrincipal p)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Authentication required");
        }
        return p.getUser();
    }
}
