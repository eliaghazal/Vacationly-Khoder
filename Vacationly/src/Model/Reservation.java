package Model;

import java.io.Serializable;
import java.time.LocalDate;

public class Reservation implements Serializable {
    private String id;
    private String clientId;
    private Place_Base place; // Changed from placeId to Place_Base object for easier access in View
    private LocalDate date;
    private double totalCost;
    private boolean isConfirmed;

    public Reservation(String id, String clientId, Place_Base place, LocalDate date, double totalCost) {
        this.id = id;
        this.clientId = clientId;
        this.place = place;
        this.date = date;
        this.totalCost = totalCost;
        this.isConfirmed = true;
    }

    public String getId() { return id; }
    public String getClientId() { return clientId; }
    public Place_Base getPlace() { return place; }
    public LocalDate getDate() { return date; }
    public double getTotalCost() { return totalCost; }
    
    // Status string for the Table
    public String getStatus() { 
        return isConfirmed ? "Confirmed" : "Cancelled"; 
    }
    
    // Details string for the Table
    public String getDetails() {
        return place.getName() + " ($" + totalCost + ")";
    }

    public boolean isConfirmed() { return isConfirmed; }
    public void setConfirmed(boolean confirmed) { this.isConfirmed = confirmed; }

    @Override
    public String toString() {
        return "Res#" + id + " : " + place.getName();
    }
}