package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Place_Base implements Serializable {
    private String id;
    private String name;
    private String description;
    private String location;
    private double basePrice;
    private boolean isTrending;
    private String specialOffer; // e.g., "10% off" or null if none
    private List<Review> reviews;

    public Place_Base(String id, String name, String description, String location, double basePrice) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.basePrice = basePrice; // Fixed: Matches the argument name now
        this.reviews = new ArrayList<>();
        this.isTrending = false;
        this.specialOffer = "None";
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public double getBasePrice() { return basePrice; }
    public boolean isTrending() { return isTrending; }
    public String getSpecialOffer() { return specialOffer; }
    public List<Review> getReviews() { return reviews; }

    public void setTrending(boolean trending) { isTrending = trending; }
    public void setSpecialOffer(String offer) { this.specialOffer = offer; }
    public void setBasePrice(double price) { this.basePrice = price; }
    
    public void addReview(Review review) {
        this.reviews.add(review);
    }
    
    public double getAverageRating() {
        if (reviews.isEmpty()) return 0.0;
        double sum = 0;
        for (Review r : reviews) sum += r.getRating();
        return sum / reviews.size();
    }
    
    // Abstract method that subclasses must implement
    public abstract String getCategory();
    
    @Override
    public String toString() {
        return name + " (" + getCategory() + ") - $" + basePrice;
    }
}