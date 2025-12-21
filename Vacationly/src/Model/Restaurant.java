package Model;

public class Restaurant extends Place_Base {
    public Restaurant(String id, String ownerId, String name, String desc, String loc, double price, int capacity) {
        super(id, ownerId, name, desc, loc, price, capacity);
    }
    @Override 
    public String getCategory() { return "Restaurant"; }
}
