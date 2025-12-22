package edu.example.library.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "employees")
public class EmployeeProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserAccount user;

    @Column(nullable = false, length = 120)
    private String fullName;

    public EmployeeProfile() {
    }

    public EmployeeProfile(UserAccount user, String fullName) {
        this.user = user;
        this.fullName = fullName;
    }

    public Long getId() {
        return id;
    }

    public UserAccount getUser() {
        return user;
    }

    public void setUser(UserAccount user) {
        this.user = user;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
