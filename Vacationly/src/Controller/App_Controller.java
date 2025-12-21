package Controller;

import Model.*;
import Utility.Data_Store;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class App_Controller {
    
    private User_Base currentUser;
    
    public App_Controller() {
        // Initialize Data Store
        Data_Store.getInstance().init();
    }
    
    // --- Authentication ---
    public User_Base login(String username, String password, boolean isAdmin) {
        for (User_Base u : Data_Store.getInstance().getUsers()) {
            if (u.getUsername().equals(username) && u.checkPassword(password)) {
                // Check Role
                if (isAdmin && u instanceof Admin) {
                    currentUser = u;
                    return u;
                } else if (!isAdmin && u instanceof Client) {
                    currentUser = u;
                    return u;
                }
            }
        }
        return null;
    }

    public void registerClient(String username, String password, String fullName) {
        String id = "C" + (Data_Store.getInstance().getUsers().size() + 1);
        Client newClient = new Client(id, username, password, fullName);
        Data_Store.getInstance().getUsers().add(newClient);
        Data_Store.getInstance().saveData();
    }

    public void logout() {
        currentUser = null;
    }
    
    public User_Base getCurrentUser() {
        return currentUser;
    }

    // --- Data Access for Views ---
    
    public List<Place_Base> getAllPlaces() {
        return Data_Store.getInstance().getPlaces();
    }
    
    public List<Message> getMessages() {
        return Data_Store.getInstance().getMessages();
    }
    
    // Used by Client Dashboard
    public List<Reservation> getClientReservations() {
        if (currentUser == null) return new ArrayList<>();
        return Data_Store.getInstance().getReservations().stream()
                .filter(r -> r.getClientId().equals(currentUser.getId()))
                .collect(Collectors.toList());
    }

    // --- THIS WAS MISSING AND CAUSED YOUR ERROR ---
    public List<Reservation> getAllReservations() {
        return Data_Store.getInstance().getReservations();
    }

    // --- Actions ---

    public void makeReservation(Place_Base place, LocalDate date) {
        if (currentUser == null) return;
        String resId = "R" + (Data_Store.getInstance().getReservations().size() + 1);
        Reservation res = new Reservation(resId, currentUser.getId(), place, date, place.getBasePrice());
        Data_Store.getInstance().getReservations().add(res);
        Data_Store.getInstance().saveData();
    }

    public void cancelReservationClient(Reservation r) {
        r.setConfirmed(false);
        Data_Store.getInstance().saveData();
    }
    
    // Used by Admin Dashboard
    public void updateReservationStatus(Reservation r, boolean confirmed) {
        r.setConfirmed(confirmed);
        Data_Store.getInstance().saveData();
    }

    public void sendSupportMessage(String content) {
        if (currentUser == null) return;
        Message msg = new Message(currentUser.getId(), currentUser.getFullName(), content);
        Data_Store.getInstance().getMessages().add(msg);
        Data_Store.getInstance().saveData();
    }

    public void respondToMessage(Message m, String response) {
        m.respond(response);
        Data_Store.getInstance().saveData();
    }
}