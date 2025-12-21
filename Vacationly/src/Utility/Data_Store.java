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
        if (instance == null) { instance = new Data_Store(); }
        return instance;
    }

    public void init() {
        if (!loadData()) { generateDummyData(); }
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
        } catch (IOException e) { System.out.println("Error saving: " + e.getMessage()); }
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
        } catch (Exception e) { return false; }
    }

    private void generateDummyData() {
        users.add(new Admin("A1", "admin", "admin123", "System Administrator"));
        Client c1 = new Client("C1", "client", "123", "Rami Hadi", 2000.0, "1234-5678");
        users.add(c1);
        BusinessOwner o1 = new BusinessOwner("O1", "owner", "123", "John Owner", "john@hotel.com");
        users.add(o1);

        Place_Base p1 = new Hotel("P1", o1.getId(), "Hotel Albergo", "Luxury heritage hotel.", "Beirut", 250.0, 10);
        p1.setApproved(true);
        p1.addReview(new Review("John", 5, "Amazing architecture!"));
        
        Place_Base p2 = new Restaurant("P2", o1.getId(), "Em Sherif", "Authentic oriental dining.", "Beirut", 80.0, 15);
        p2.setApproved(true);
        p2.setSpecialOffer("Free Dessert");
        
        places.add(p1);
        places.add(p2);
        
        // Use Room 1 for dummy reservation
        reservations.add(new Reservation("R1", c1.getId(), p1, "Room 1", LocalDate.now(), LocalDate.now().plusDays(1), 250.0));
        messages.add(new Message(c1.getId(), c1.getFullName(), o1.getId(), "Is breakfast included?"));
    }
}
