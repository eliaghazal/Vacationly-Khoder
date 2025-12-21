package Model;

public class Hotel extends Place_Base {
    public Hotel(String id, String name, String desc, String loc, double price) {
        super(id, name, desc, loc, price);
    }
    @Override 
    public String getCategory() { return "Hotel"; }
}