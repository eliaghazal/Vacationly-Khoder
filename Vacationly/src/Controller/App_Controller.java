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

    public void addPlace(Place_Base p) {
        Data_Store.getInstance().getPlaces().add(p);
        Data_Store.getInstance().saveData();
    }
    
    // --- Admin Services (Restored for Admin Dashboard) ---
    public List<Place_Base> getAllPlaces() {
        return Data_Store.getInstance().getPlaces();
    }

    public List<Reservation> getAllReservations() {
        return Data_Store.getInstance().getReservations();
    }
    
    public List<Message> getMessages() {
        return Data_Store.getInstance().getMessages();
    }

    public void updateReservationStatus(Reservation r, boolean confirmed) {
        r.setStatus(confirmed ? Reservation.ReservationStatus.CONFIRMED : Reservation.ReservationStatus.CANCELED);
        Data_Store.getInstance().saveData();
    }
    
    public void respondToMessage(Message m, String response) {
        m.respond(response);
        Data_Store.getInstance().saveData();
    }

    // --- Booking Service ---
    public boolean checkAvailability(Place_Base place, LocalDate start, LocalDate end) {
        for (Reservation r : Data_Store.getInstance().getReservations()) {
            if (r.getPlace().getId().equals(place.getId()) && 
               (r.getStatus() == Reservation.ReservationStatus.CONFIRMED)) {
                
                if (!start.isAfter(r.getEndDate()) && !end.isBefore(r.getStartDate())) {
                    return false; 
                }
            }
        }
        return true;
    }

    public void createReservation(Place_Base place, LocalDate start, LocalDate end) throws Exception {
        if (!(currentUser instanceof Client)) throw new Exception("Only clients can book.");
        Client client = (Client) currentUser;

        long days = ChronoUnit.DAYS.between(start, end);
        if(days < 1) days = 1; 
        double total = place.getBasePrice() * days;

        if (client.getBalance() < total) {
            throw new Exception("Insufficient Balance.");
        }
        
        if (!checkAvailability(place, start, end)) {
            throw new Exception("Place is fully booked.");
        }

        client.deductBalance(total);
        
        String rid = "R" + System.currentTimeMillis();
        Reservation res = new Reservation(rid, client.getId(), place, start, end, total);
        res.setStatus(Reservation.ReservationStatus.CONFIRMED);
        
        Data_Store.getInstance().getReservations().add(res);
        Data_Store.getInstance().saveData();
    }

    public void cancelReservation(Reservation r) {
        if (r.getStatus() == Reservation.ReservationStatus.CONFIRMED) {
           // Refund logic simplified
            if (currentUser instanceof Client) {
                Client c = (Client) currentUser;
                c.setBalance(c.getBalance() + r.getTotalCost());
            }
            r.setStatus(Reservation.ReservationStatus.CANCELED);
            Data_Store.getInstance().saveData();
        }
    }

    public List<Reservation> getMyReservations() {
        if (currentUser == null) return new ArrayList<>();
        if (currentUser instanceof Client) {
            return Data_Store.getInstance().getReservations().stream()
                    .filter(r -> r.getClientId().equals(currentUser.getId()))
                    .collect(Collectors.toList());
        } else if (currentUser instanceof BusinessOwner) {
            return Data_Store.getInstance().getReservations().stream()
                    .filter(r -> r.getPlace().getOwnerId().equals(currentUser.getId()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
    
    public void sendSupportMessage(String content) {
        if (currentUser == null) return;
        Message msg = new Message(currentUser.getId(), currentUser.getFullName(), content);
        Data_Store.getInstance().getMessages().add(msg);
        Data_Store.getInstance().saveData();
    }
}
