package View;

import Controller.App_Controller;
import Model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class Client_Dashboard extends JFrame {
    private App_Controller controller;
    private JTable placesTable;
    private DefaultTableModel placesModel;
    private JTable resTable;
    private DefaultTableModel resModel;
    
    // Filter controls
    private JTextField searchField;
    private JCheckBox trendingCheck, offerCheck;
    private JTextField maxPriceField;
    
    // List to hold filtered places for index matching
    private List<Place_Base> displayedPlaces;

    public Client_Dashboard(App_Controller controller) {
        this.controller = controller;
        setTitle("Vacationly - Discover Lebanon");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Browse Places", createBrowsePanel());
        tabs.addTab("My Reservations", createReservationsPanel());
        tabs.addTab("Support", createSupportPanel());
        
        // Add Logout
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            controller.logout();
            this.dispose();
            new Login_View(controller).setVisible(true);
        });
        
        JPanel header = new JPanel(new BorderLayout());
        header.add(new JLabel("Welcome, " + controller.getCurrentUser().getFullName()), BorderLayout.WEST);
        header.add(topPanel, BorderLayout.EAST);
        
        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }
    
    private JPanel createBrowsePanel() {
        JPanel p = new JPanel(new BorderLayout());
        
        // Filter Bar
        JPanel filterPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(10);
        trendingCheck = new JCheckBox("Trending Only");
        offerCheck = new JCheckBox("With Offers");
        maxPriceField = new JTextField("1000", 5);
        JButton filterBtn = new JButton("Apply Filters");
        
        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(trendingCheck);
        filterPanel.add(offerCheck);
        filterPanel.add(new JLabel("Max Price:"));
        filterPanel.add(maxPriceField);
        filterPanel.add(filterBtn);
        
        // Table
        String[] cols = {"Name", "Category", "Location", "Price", "Special"};
        placesModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        placesTable = new JTable(placesModel);
        
        // Action Panel
        JPanel actionPanel = new JPanel();
        JButton bookBtn = new JButton("Make Reservation");
        
        bookBtn.addActionListener(e -> {
            int row = placesTable.getSelectedRow();
            if(row != -1) {
                // Ensure we pick from the filtered list (displayedPlaces) not the full list
                if (row < displayedPlaces.size()) {
                    Place_Base place = displayedPlaces.get(row);
                    int choice = JOptionPane.showConfirmDialog(this, 
                            "Book " + place.getName() + " for $" + place.getBasePrice() + "?");
                    
                    if(choice == JOptionPane.YES_OPTION) {
                        controller.makeReservation(place, LocalDate.now()); // Using today's date for simplicity
                        JOptionPane.showMessageDialog(this, "Reservation Confirmed!");
                        loadReservations(); // Refresh reservation tab
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a place first.");
            }
        });
        
        actionPanel.add(bookBtn);
        
        p.add(filterPanel, BorderLayout.NORTH);
        p.add(new JScrollPane(placesTable), BorderLayout.CENTER);
        p.add(actionPanel, BorderLayout.SOUTH);
        
        filterBtn.addActionListener(e -> loadPlaces());
        
        loadPlaces(); // Initial Load
        return p;
    }
    
    private void loadPlaces() {
        // Get all places
        List<Place_Base> all = controller.getAllPlaces();
        
        // Apply Filters
        String search = searchField.getText().toLowerCase();
        double maxPrice = Double.MAX_VALUE;
        try {
            maxPrice = Double.parseDouble(maxPriceField.getText());
        } catch (NumberFormatException e) { /* ignore */ }
        
        double finalMaxPrice = maxPrice;
        displayedPlaces = all.stream()
            .filter(pl -> pl.getName().toLowerCase().contains(search) || pl.getLocation().toLowerCase().contains(search))
            .filter(pl -> !trendingCheck.isSelected() || pl.isTrending())
            .filter(pl -> !offerCheck.isSelected() || !"None".equals(pl.getSpecialOffer()))
            .filter(pl -> pl.getBasePrice() <= finalMaxPrice)
            .collect(Collectors.toList());
            
        // Update Table
        placesModel.setRowCount(0);
        for(Place_Base pl : displayedPlaces) {
            placesModel.addRow(new Object[]{
                pl.getName(),
                pl.getCategory(),
                pl.getLocation(),
                "$" + pl.getBasePrice(),
                pl.getSpecialOffer()
            });
        }
    }
    
    private JPanel createReservationsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        String[] cols = {"ID", "Place", "Date", "Details", "Status"};
        resModel = new DefaultTableModel(cols, 0);
        resTable = new JTable(resModel);
        
        JButton cancelBtn = new JButton("Cancel Reservation");
        cancelBtn.addActionListener(e -> {
             int row = resTable.getSelectedRow();
             if(row != -1) {
                 List<Reservation> clientRes = controller.getClientReservations();
                 if (row < clientRes.size()) {
                     Reservation r = clientRes.get(row);
                     controller.cancelReservationClient(r);
                     loadReservations();
                 }
             } else {
                 JOptionPane.showMessageDialog(this, "Select a reservation to cancel.");
             }
        });
        
        p.add(new JScrollPane(resTable), BorderLayout.CENTER);
        p.add(cancelBtn, BorderLayout.SOUTH);
        
        loadReservations();
        return p;
    }
    
    private void loadReservations() {
        resModel.setRowCount(0);
        for(Reservation r : controller.getClientReservations()) {
            resModel.addRow(new Object[]{
                r.getId(), 
                r.getPlace().getName(), 
                r.getDate(), 
                r.getDetails(), 
                r.getStatus()
            });
        }
    }
    
    private JPanel createSupportPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JTextArea msgArea = new JTextArea();
        JButton sendBtn = new JButton("Send to Support");
        
        sendBtn.addActionListener(e -> {
            if(!msgArea.getText().isEmpty()){
                controller.sendSupportMessage(msgArea.getText());
                msgArea.setText("");
                JOptionPane.showMessageDialog(this, "Message Sent");
            }
        });
        
        p.add(new JLabel("Describe your issue:"), BorderLayout.NORTH);
        p.add(new JScrollPane(msgArea), BorderLayout.CENTER);
        p.add(sendBtn, BorderLayout.SOUTH);
        return p;
    }
}