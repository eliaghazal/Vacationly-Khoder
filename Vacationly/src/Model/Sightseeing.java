package Model;

public class Sightseeing extends Place_Base {
    public Sightseeing(String id, String name, String desc, String loc, double price) {
        super(id, name, desc, loc, price);
    }
    @Override 
    public String getCategory() { return "Sightseeing"; }
}