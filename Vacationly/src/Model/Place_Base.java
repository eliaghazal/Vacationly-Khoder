package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Place_Base implements Serializable {
    private String id;
    private String ownerId;
    private String name;
    private String description;
    private String location;
    private double basePrice;
    private boolean isTrending;
    private boolean isApproved;
    private String specialOffer; 
    private List<Review> reviews;
    private List<String> units; // e.g., "Room 101", "Table 4"

    public Place_Base(String id, String ownerId, String name, String description, String location, double basePrice, int capacity) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.location = location;
        this.basePrice = basePrice;
        this.reviews = new ArrayList<>();
        this.units = new ArrayList<>();
        this.isTrending = false;
        this.isApproved = false;
        this.specialOffer = "None";
        
        // Auto-generate units for simulation purposes
        generateUnits(capacity);
    }

    private void generateUnits(int cap) {
        String prefix = getCategory().equals("Restaurant") ? "Table " : "Room ";
        for(int i=1; i<=cap; i++) {
            units.add(prefix + i);
        }
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getOwnerId() { return ownerId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public double getBasePrice() { return basePrice; }
    public boolean isTrending() { return isTrending; }
    public boolean isApproved() { return isApproved; }
    public String getSpecialOffer() { return specialOffer; }
    public List<Review> getReviews() { return reviews; }
    public List<String> getUnits() { return units; }

    public void setApproved(boolean approved) { this.isApproved = approved; }
    public void setTrending(boolean trending) { isTrending = trending; }
    public void setSpecialOffer(String offer) { this.specialOffer = offer; }
    public void addReview(Review review) { this.reviews.add(review); }

    public abstract String getCategory();
    
    @Override
    public String toString() { return name; }
}
