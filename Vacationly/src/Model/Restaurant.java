package Model;

public class Restaurant extends Place_Base {
    public Restaurant(String id, String name, String desc, String loc, double price) {
        super(id, name, desc, loc, price);
    }
    @Override 
    public String getCategory() { return "Restaurant"; }
}