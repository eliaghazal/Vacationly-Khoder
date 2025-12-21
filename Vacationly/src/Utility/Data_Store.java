package Utility;

import Model.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Data_Store implements Serializable {
    private static final long serialVersionUID = 1L;
    private static Data_Store instance;
    private static final String FILE_NAME = "vacationly_data.ser";

    private List<User_Base> users;
    private List<Place_Base> places;
    private List<Reservation> reservations;
    private List<Message> messages;

    private Data_Store() {
        users = new ArrayList<>();
        places = new ArrayList<>();
        reservations = new ArrayList<>();
        messages = new ArrayList<>();
    }

    public static Data_Store getInstance() {
        if (instance == null) {
            instance = new Data_Store();
        }
        return instance;
    }

    public void init() {
        // Try to load, if fail, generate dummy
        if (!loadData()) {
            generateDummyData();
        }
    }

    public List<User_Base> getUsers() { return users; }
    public List<Place_Base> getPlaces() { return places; }
    public List<Reservation> getReservations() { return reservations; }
    public List<Message> getMessages() { return messages; }

    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(users);
            oos.writeObject(places);
            oos.writeObject(reservations);
            oos.writeObject(messages);
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private boolean loadData() {
        File f = new File(FILE_NAME);
        if (!f.exists()) return false;
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            users = (List<User_Base>) ois.readObject();
            places = (List<Place_Base>) ois.readObject();
            reservations = (List<Reservation>) ois.readObject();
            messages = (List<Message>) ois.readObject();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void generateDummyData() {
        // Users
        users.add(new Admin("A1", "admin", "admin123", "Business Owner", "Lebanon Travels"));
        Client c1 = new Client("C1", "client", "123", "Rami Hadi");
        users.add(c1);

        // Places
        Place_Base p1 = new Hotel("P1", "Hotel Albergo", "Luxury heritage hotel.", "Beirut", 250.0);
        p1.setTrending(true);
        p1.addReview(new Review("John", 5, "Amazing architecture!"));
        
        Place_Base p2 = new Restaurant("P2", "Em Sherif", "Authentic oriental fine dining.", "Beirut", 80.0);
        p2.setSpecialOffer("Free Dessert");
        
        places.add(p1);
        places.add(p2);
        places.add(new Resort("P3", "Edde Sands", "Beach resort.", "Byblos", 40.0));
        places.add(new Sightseeing("P4", "Jeita Grotto", "Limestone caves.", "Jeita", 20.0));
        
        // Dummy Reservation for the client to see
        reservations.add(new Reservation("R1", c1.getId(), p1, LocalDate.now(), 250.0));
        
        // Dummy Message
        messages.add(new Message(c1.getId(), c1.getFullName(), "Is breakfast included?"));
    }
}