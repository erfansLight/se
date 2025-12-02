package edu.example.library;

import edu.example.library.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
public class AuthServiceTest {
    private AuthService auth;
    @BeforeEach public void setup(){ auth = new AuthService(); }
    @Test public void registerUniqueReturnsTrue(){ assertTrue(auth.register("s1","p")); }
    @Test public void registerDuplicateReturnsFalse(){ auth.register("s1","p"); assertFalse(auth.register("s1","p2")); }
    @Test public void loginCorrectReturnsTrue(){ auth.register("s2","p2"); assertTrue(auth.login("s2","p2")); }
    @Test public void loginWrongPasswordReturnsFalse(){ auth.register("s3","p3"); assertFalse(auth.login("s3","wrong")); }
    @Test public void loginNonexistentReturnsFalse(){ assertFalse(auth.login("no","x")); }
}
