package Model;

import java.io.Serializable;

public abstract class User_Base implements Serializable {
    private String id;
    private String username;
    private String password;
    private String fullName;

    public User_Base(String id, String username, String password, String fullName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
    }

    public String getUsername() { return username; }
    public boolean checkPassword(String pass) { return this.password.equals(pass); }
    public String getFullName() { return fullName; }
    public String getId() { return id; }
    
    // Used for display
    @Override
    public String toString() { return fullName; }
}