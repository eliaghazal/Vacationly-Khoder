package Model;

public class Resort extends Place_Base {
    public Resort(String id, String name, String desc, String loc, double price) {
        super(id, name, desc, loc, price);
    }
    @Override 
    public String getCategory() { return "Resort"; }
}