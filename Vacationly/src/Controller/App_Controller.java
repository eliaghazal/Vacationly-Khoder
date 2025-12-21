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
    
    // --- Auth ---
    public User_Base login(String username, String password) {
        for (User_Base u : Data_Store.getInstance().getUsers()) {
            if (u.getUsername().equals(username) && u.checkPassword(password)) {
                currentUser = u;
                return u;
            }
        }
        return null;
    }

    public void logout() { currentUser = null; }
    public User_Base getCurrentUser() { return currentUser; }

    // --- Place Data ---
    public List<Place_Base> getApprovedPlaces() {
        return Data_Store.getInstance().getPlaces().stream().filter(Place_Base::isApproved).collect(Collectors.toList());
    }
    
    public List<Place_Base> getOwnerPlaces(String ownerId) {
        return Data_Store.getInstance().getPlaces().stream().filter(p -> p.getOwnerId().equals(ownerId)).collect(Collectors.toList());
    }
    
    public void addPlace(Place_Base p) {
        Data_Store.getInstance().getPlaces().add(p);
        Data_Store.getInstance().saveData();
    }

    public void addReview(Place_Base place, int rating, String comment) {
        if(currentUser != null) {
            place.addReview(new Review(currentUser.getFullName(), rating, comment));
            Data_Store.getInstance().saveData();
        }
    }

    // --- Booking Logic ---
    public List<String> getAvailableUnits(Place_Base place, LocalDate start, LocalDate end) {
        List<String> available = new ArrayList<>(place.getUnits());
        
        for (Reservation r : Data_Store.getInstance().getReservations()) {
            if (r.getPlace().getId().equals(place.getId()) && 
               (r.getStatus() == Reservation.ReservationStatus.CONFIRMED)) {
                
                // If dates overlap, remove the unit
                if (!start.isAfter(r.getEndDate()) && !end.isBefore(r.getStartDate())) {
                    available.remove(r.getUnitName());
                }
            }
        }
        return available;
    }

    public void createReservation(Place_Base place, String unit, LocalDate start, LocalDate end) throws Exception {
        if (!(currentUser instanceof Client)) throw new Exception("Only clients can book.");
        Client client = (Client) currentUser;

        long days = ChronoUnit.DAYS.between(start, end);
        if(days < 1) days = 1; 
        double total = place.getBasePrice() * days;

        if (client.getBalance() < total) throw new Exception("Insufficient Balance.");
        
        client.deductBalance(total);
        
        String rid = "R" + System.currentTimeMillis();
        Reservation res = new Reservation(rid, client.getId(), place, unit, start, end, total);
        res.setStatus(Reservation.ReservationStatus.CONFIRMED);
        
        Data_Store.getInstance().getReservations().add(res);
        Data_Store.getInstance().saveData();
    }

    public void cancelReservation(Reservation r) {
        if (r.getStatus() != Reservation.ReservationStatus.CANCELED) {
            // Refund
            User_Base u = Data_Store.getInstance().getUserById(r.getClientId());
            if(u instanceof Client) {
                ((Client)u).setBalance(((Client)u).getBalance() + r.getTotalCost());
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
    
    // --- Messaging ---
    public void sendMessage(String recipientId, String content) {
        if (currentUser == null) return;
        Message msg = new Message(currentUser.getId(), currentUser.getFullName(), recipientId, content);
        Data_Store.getInstance().getMessages().add(msg);
        Data_Store.getInstance().saveData();
    }
    
    public void replyMessage(Message m, String reply) {
        m.respond(reply);
        Data_Store.getInstance().saveData();
    }
    
    public List<Message> getMyMessages() {
        if (currentUser == null) return new ArrayList<>();
        // Get messages where I am the recipient
        return Data_Store.getInstance().getMessages().stream()
                .filter(m -> m.getRecipientId().equals(currentUser.getId()))
                .collect(Collectors.toList());
    }

    // --- Admin ---
    public List<Place_Base> getAllPlaces() { return Data_Store.getInstance().getPlaces(); }
    public List<Reservation> getAllReservations() { return Data_Store.getInstance().getReservations(); }
    public List<Message> getMessages() { return Data_Store.getInstance().getMessages(); }
    public void updateReservationStatus(Reservation r, boolean confirmed) {
        r.setStatus(confirmed ? Reservation.ReservationStatus.CONFIRMED : Reservation.ReservationStatus.CANCELED);
        Data_Store.getInstance().saveData();
    }
    public void respondToMessage(Message m, String response) { m.respond(response); Data_Store.getInstance().saveData(); }
    
    // Registration methods
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
}
