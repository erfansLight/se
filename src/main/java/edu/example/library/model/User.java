package edu.example.library.model;
public class User {
    private final String username;
    private String password;
    private final UserRole role;
    private boolean active = true;
    public User(String username, String password, UserRole role) {
        this.username = username; this.password = password; this.role = role;
    }
    public String getUsername(){return username;}
    public String getPassword(){return password;}
    public void setPassword(String p){this.password = p;}
    public UserRole getRole(){return role;}
    public boolean isActive(){return active;}
    public void setActive(boolean a){this.active = a;}
    @Override public String toString(){ return username + " (" + role + ") active=" + active; }
}
