package Model;

public class Admin extends User_Base {
    public Admin(String id, String username, String password, String fullName) {
        super(id, username, password, fullName, "ADMIN");
    }
}
