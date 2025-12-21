package Model;

public class Sightseeing extends Place_Base {
    public Sightseeing(String id, String ownerId, String name, String desc, String loc, double price, int capacity) {
        super(id, ownerId, name, desc, loc, price, capacity);
    }
    @Override 
    public String getCategory() { return "Sightseeing"; }
}
