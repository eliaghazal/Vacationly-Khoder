package Model;

public class Hotel extends Place_Base {
    public Hotel(String id, String ownerId, String name, String desc, String loc, double price, int capacity) {
        super(id, ownerId, name, desc, loc, price, capacity);
    }
    @Override 
    public String getCategory() { return "Hotel"; }
}
