package Controller;

import Model.*;
import Utility.Data_Store;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class App_Controller {
    
    private User_Base currentUser;
    
    public App_Controller() {
        Data_Store.getInstance().init();
    }
    
    // --- Auth Service ---
    public User_Base login(String username, String password) {
        for (User_Base u : Data_Store.getInstance().getUsers()) {
            if (u.getUsername().equals(username) && u.checkPassword(password)) {
                currentUser = u;
                return u;
            }
        }
        return null;
    }

    public void registerClient(String name, String user, String pass, double bal, String card) {
        String id = "C" + System.currentTimeMillis();
        Data_Store.getInstance().getUsers().add(new Client(id, user, pass, name, bal, card));
        Data_Store.getInstance().saveData();
    }

    public void registerOwner(String name, String user, String pass, String contact) {
        String id = "O" + System.currentTimeMillis();
        Data_Store.getInstance().getUsers().add(new BusinessOwner(id, user, pass, name, contact));
        Data_Store.getInstance().saveData();
    }

    public void logout() { currentUser = null; }
    public User_Base getCurrentUser() { return currentUser; }

    // --- Place Service ---
    public List<Place_Base> getApprovedPlaces() {
        return Data_Store.getInstance().getPlaces().stream()
                .filter(Place_Base::isApproved)
                .collect(Collectors.toList());
    }
    
    public List<Place_Base> getOwnerPlaces(String ownerId) {
        return Data_Store.getInstance().getPlaces().stream()
                .filter(p -> p.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    public List<Place_Base> getAllPlacesForAdmin() {
        return Data_Store.getInstance().getPlaces();
    }

    public void addPlace(Place_Base p) {
        Data_Store.getInstance().getPlaces().add(p);
        Data_Store.getInstance().saveData();
    }
    
    public void approvePlace(Place_Base p) {
        p.setApproved(true);
        Data_Store.getInstance().saveData();
    }

    // --- Booking Service (Availability & Payment) ---
    public boolean checkAvailability(Place_Base place, LocalDate start, LocalDate end) {
        // Simple overlap check: (StartA <= EndB) and (EndA >= StartB)
        for (Reservation r : Data_Store.getInstance().getReservations()) {
            if (r.getPlace().getId().equals(place.getId()) && 
               (r.getStatus() == Reservation.ReservationStatus.CONFIRMED)) {
                
                if (!start.isAfter(r.getEndDate()) && !end.isBefore(r.getStartDate())) {
                    return false; // Overlap found
                }
            }
        }
        return true;
    }

    public void createReservation(Place_Base place, LocalDate start, LocalDate end) throws Exception {
        if (!(currentUser instanceof Client)) throw new Exception("Only clients can book.");
        Client client = (Client) currentUser;

        long days = ChronoUnit.DAYS.between(start, end);
        if(days < 1) days = 1; // Minimum 1 day/slot
        double total = place.getBasePrice() * days;

        // Balance Check
        if (client.getBalance() < total) {
            throw new Exception("Insufficient Balance. Wallet: $" + client.getBalance() + ", Cost: $" + total);
        }
        
        // Availability Check
        if (!checkAvailability(place, start, end)) {
            throw new Exception("Place is fully booked for these dates.");
        }

        // Process Payment
        client.deductBalance(total);
        
        // Save Reservation
        String rid = "R" + System.currentTimeMillis();
        Reservation res = new Reservation(rid, client.getId(), place, start, end, total);
        res.setStatus(Reservation.ReservationStatus.CONFIRMED);
        
        Data_Store.getInstance().getReservations().add(res);
        Data_Store.getInstance().saveData(); // Persist changes to Client balance and Reservation list
    }

    public void cancelReservation(Reservation r) {
        if (r.getStatus() == Reservation.ReservationStatus.CONFIRMED) {
            // Refund logic could go here (e.g., 100% refund)
            if (currentUser instanceof Client) {
                Client c = (Client) currentUser;
                c.setBalance(c.getBalance() + r.getTotalCost());
            } else {
                // If admin cancels, find client and refund
                for(User_Base u : Data_Store.getInstance().getUsers()) {
                    if(u.getId().equals(r.getClientId()) && u instanceof Client) {
                        ((Client)u).setBalance(((Client)u).getBalance() + r.getTotalCost());
                    }
                }
            }
            r.setStatus(Reservation.ReservationStatus.CANCELED);
            Data_Store.getInstance().saveData();
        }
    }

    public List<Reservation> getMyReservations() {
        if (currentUser == null) return new ArrayList<>();
        // If Client -> filter by clientId
        // If Owner -> filter by places owned by owner
        // If Admin -> return all
        
        if (currentUser instanceof Client) {
            return Data_Store.getInstance().getReservations().stream()
                    .filter(r -> r.getClientId().equals(currentUser.getId()))
                    .collect(Collectors.toList());
        } else if (currentUser instanceof BusinessOwner) {
            return Data_Store.getInstance().getReservations().stream()
                    .filter(r -> r.getPlace().getOwnerId().equals(currentUser.getId()))
                    .collect(Collectors.toList());
        } else {
            return Data_Store.getInstance().getReservations();
        }
    }
}
