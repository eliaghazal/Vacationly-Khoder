package Model;

import java.io.Serializable;
import java.time.LocalDate;

public class Reservation implements Serializable {
    private String id;
    private String clientId;
    private Place_Base place;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalCost;
    private ReservationStatus status;

    public enum ReservationStatus {
        PENDING, CONFIRMED, CANCELED, COMPLETED
    }

    public Reservation(String id, String clientId, Place_Base place, LocalDate startDate, LocalDate endDate, double totalCost) {
        this.id = id;
        this.clientId = clientId;
        this.place = place;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalCost = totalCost;
        this.status = ReservationStatus.PENDING;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getClientId() { return clientId; }
    public Place_Base getPlace() { return place; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public double getTotalCost() { return totalCost; }
    public ReservationStatus getStatus() { return status; }
    
    public void setStatus(ReservationStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "Res#" + id + " [" + status + "] " + place.getName();
    }
}
