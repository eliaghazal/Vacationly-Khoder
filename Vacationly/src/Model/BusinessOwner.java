package Model;

public class BusinessOwner extends User_Base {
    private String contactInfo;

    public BusinessOwner(String id, String username, String password, String fullName, String contactInfo) {
        super(id, username, password, fullName, "OWNER");
        this.contactInfo = contactInfo;
    }
    
    public String getContactInfo() { return contactInfo; }
}
