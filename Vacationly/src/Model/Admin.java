package Model;

public class Admin extends User_Base {
    private String businessName;

    public Admin(String id, String username, String password, String fullName, String businessName) {
        super(id, username, password, fullName);
        this.businessName = businessName;
    }

    public String getBusinessName() { return businessName; }
}