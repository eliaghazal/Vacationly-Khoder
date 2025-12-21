package Model;

public class Resort extends Place_Base {
    public Resort(String id, String ownerId, String name, String desc, String loc, double price, int capacity) {
        super(id, ownerId, name, desc, loc, price, capacity);
    }
    @Override 
    public String getCategory() { return "Resort"; }
}
