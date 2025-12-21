package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Place_Base implements Serializable {
    private String id;
    private String ownerId; // Link to BusinessOwner
    private String name;
    private String description;
    private String location;
    private double basePrice;
    private boolean isTrending;
    private boolean isApproved; // Admin approval
    private String specialOffer; 
    private List<Review> reviews;
    private int capacity; // General capacity for simplicity

    public Place_Base(String id, String ownerId, String name, String description, String location, double basePrice, int capacity) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.location = location;
        this.basePrice = basePrice;
        this.capacity = capacity;
        this.reviews = new ArrayList<>();
        this.isTrending = false;
        this.isApproved = false; // Default to not approved
        this.specialOffer = "None";
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
    public int getCapacity() { return capacity; }

    public void setApproved(boolean approved) { this.isApproved = approved; }
    public void setTrending(boolean trending) { isTrending = trending; }
    public void setSpecialOffer(String offer) { this.specialOffer = offer; }
    public void addReview(Review review) { this.reviews.add(review); }

    public abstract String getCategory();
    
    @Override
    public String toString() { return name + " - " + getCategory(); }
}
