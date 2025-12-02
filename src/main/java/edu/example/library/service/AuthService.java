package edu.example.library.service;

import edu.example.library.model.User;
import edu.example.library.model.UserRole;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
public class AuthService {
    private final Map<String, User> users = new LinkedHashMap<>();
    public AuthService(){
        users.put("admin", new User("admin","admin", UserRole.ADMIN));
    }
    public boolean register(String username, String password){
        if(username==null || username.isBlank() || password==null) return false;
        if(users.containsKey(username)) return false;
        users.put(username, new User(username,password, UserRole.STUDENT));
        return true;
    }
    public boolean login(String username, String password){
        User u = users.get(username);
        if(u==null) return false;
        return u.getPassword().equals(password);
    }
    public User getUser(String username){ return users.get(username); }
    public Collection<User> allUsers(){ return users.values(); }
    public void addEmployee(String username, String password){
        users.put(username, new User(username,password, UserRole.EMPLOYEE));
    }
    // helper for seeding Users directly (used by Main)
    public void seedUser(User u){ users.put(u.getUsername(), u); }
}
