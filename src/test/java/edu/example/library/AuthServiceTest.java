package edu.example.library;

import edu.example.library.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {
    private AuthService auth;

    @BeforeEach
    public void setup(){
        auth = new AuthService();
    }

    // Scenario 1-1
    @Test
    public void registerUniqueReturnsTrue(){
        assertTrue(auth.register("s1","p"));
    }

    // Scenario 1-2
    @Test
    public void registerDuplicateReturnsFalse(){
        assertTrue(auth.register("s1","p"));
        assertFalse(auth.register("s1","p2"));
    }

    // Scenario 1-3
    @Test
    public void loginCorrectReturnsTrue(){
        auth.register("s2","p2");
        assertTrue(auth.login("s2","p2"));
    }

    // Scenario 1-4
    @Test
    public void loginWrongPasswordReturnsFalse(){
        auth.register("s3","p3");
        assertFalse(auth.login("s3","wrong"));
    }

    // Scenario 1-5
    @Test
    public void loginNonexistentReturnsFalse(){
        assertFalse(auth.login("no","x"));
    }
}
