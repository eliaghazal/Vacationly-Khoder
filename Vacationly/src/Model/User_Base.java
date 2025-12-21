package Model;

import java.io.Serializable;

public abstract class User_Base implements Serializable {
    private String id;
    private String username;
    private String password; // In a real app, hash this!
    private String fullName;
    private String role; // "CLIENT", "ADMIN", "OWNER"

    public User_Base(String id, String username, String password, String fullName, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public boolean checkPassword(String pass) { return this.password.equals(pass); }
    
    @Override
    public String toString() { return fullName + " (" + role + ")"; }
}
