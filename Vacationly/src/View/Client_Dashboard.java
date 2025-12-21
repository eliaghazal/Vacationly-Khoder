package View;

import Controller.App_Controller;
import Model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class Client_Dashboard extends JFrame {
    private App_Controller controller;
    private JTable table, bookingsTable;
    private DefaultTableModel model, bookingsModel;
    private JTextField searchField;
    private JCheckBox sortPrice;

    public Client_Dashboard(App_Controller controller) {
        this.controller = controller;
        setTitle("Vacationly - Explore & Book");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        StyleUtils.styleFrame(this);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(StyleUtils.HEADER_FONT);
        tabs.addTab("Explore Places", createBrowsePanel());
        tabs.addTab("My Trips", createBookingsPanel());
        
        // Header
        Client client = (Client) controller.getCurrentUser();
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        top.setBorder(new javax.swing.border.EmptyBorder(15, 20, 15, 20));
        
        JLabel brand = new JLabel("VACATIONLY");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 24));
        brand.setForeground(StyleUtils.PRIMARY);
        
        JLabel info = new JLabel("Hello, " + client.getFullName() + " | Wallet: $" + client.getBalance());
        info.setFont(StyleUtils.DATA_FONT);
        
        JButton logout = StyleUtils.createOutlineButton("Logout");
        logout.addActionListener(e -> { new Login_View(controller).setVisible(true); dispose(); });
        
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.add(info);
        right.add(Box.createHorizontalStrut(20));
        right.add(logout);
        
        top.add(brand, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createBrowsePanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);
        panel.setBorder(new javax.swing.border.EmptyBorder(20, 20, 20, 20));
        
        // Filter Bar
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        filters.setOpaque(false);
        searchField = new JTextField(20);
        sortPrice = new JCheckBox("Price: Low to High");
        sortPrice.setOpaque(false);
        JButton searchBtn = StyleUtils.createStyledButton("Search");
        searchBtn.addActionListener(e -> loadPlaces());
        
        filters.add(new JLabel("Find:"));
        filters.add(searchField);
        filters.add(sortPrice);
        filters.add(searchBtn);
        
        // Table
        String[] cols = {"Name", "Type", "City", "Price", "Offer"};
        model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(model);
        StyleUtils.styleTable(table);
        
        JButton viewBtn = StyleUtils.createStyledButton("View Details & Book");
        viewBtn.addActionListener(e -> openPlaceDetails());
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(viewBtn);

        panel.add(filters, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        loadPlaces();
        return panel;
    }
    
    private void openPlaceDetails() {
        int row = table.getSelectedRow();
        if(row == -1) { JOptionPane.showMessageDialog(this, "Select a place first!"); return; }
        
        String name = (String) model.getValueAt(row, 0);
        Place_Base place = controller.getApprovedPlaces().stream()
                .filter(p -> p.getName().equals(name)).findFirst().orElse(null);
        
        if(place == null) return;
        
        // Custom Dialog for Details
        JDialog dialog = new JDialog(this, place.getName(), true);
        dialog.setSize(600, 700);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        // Info Tab
        JPanel infoP = new JPanel();
        infoP.setLayout(new BoxLayout(infoP, BoxLayout.Y_AXIS));
        infoP.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        
        infoP.add(new JLabel("<html><h2>" + place.getName() + "</h2><p>"+place.getDescription()+"</p></html>"));
        infoP.add(Box.createVerticalStrut(20));
        infoP.add(new JLabel("Price per night/meal: $" + place.getBasePrice()));
        infoP.add(new JLabel("Special Offer: " + place.getSpecialOffer()));
        
        JButton contactBtn = StyleUtils.createOutlineButton("Contact Owner");
        contactBtn.addActionListener(e -> {
            String msg = JOptionPane.showInputDialog("Message for owner:");
            if(msg != null && !msg.trim().isEmpty()) {
                controller.sendMessage(place.getOwnerId(), "Regarding " + place.getName() + ": " + msg);
                JOptionPane.showMessageDialog(dialog, "Message Sent!");
            }
        });
        infoP.add(Box.createVerticalStrut(20));
        infoP.add(contactBtn);

        // Booking Tab
        JPanel bookP = new JPanel(new GridLayout(6, 1, 10, 10));
        bookP.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        JTextField sDate = new JTextField(LocalDate.now().toString());
        JTextField eDate = new JTextField(LocalDate.now().plusDays(1).toString());
        JComboBox<String> unitBox = new JComboBox<>();
        JButton checkBtn = StyleUtils.createOutlineButton("Check Availability");
        JButton confirmBtn = StyleUtils.createStyledButton("Confirm Booking");
        confirmBtn.setEnabled(false);
        
        checkBtn.addActionListener(e -> {
            try {
                LocalDate s = LocalDate.parse(sDate.getText());
                LocalDate en = LocalDate.parse(eDate.getText());
                List<String> units = controller.getAvailableUnits(place, s, en);
                unitBox.removeAllItems();
                if(units.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "No units available for these dates.");
                    confirmBtn.setEnabled(false);
                } else {
                    for(String u : units) unitBox.addItem(u);
                    confirmBtn.setEnabled(true);
                    JOptionPane.showMessageDialog(dialog, "Found " + units.size() + " available units.");
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(dialog, "Invalid Dates"); }
        });
        
        confirmBtn.addActionListener(e -> {
            try {
                controller.createReservation(place, (String)unitBox.getSelectedItem(), LocalDate.parse(sDate.getText()), LocalDate.parse(eDate.getText()));
                JOptionPane.showMessageDialog(dialog, "Success!");
                dialog.dispose();
                loadBookings();
                // Refresh balance
                this.dispose(); new Client_Dashboard(controller).setVisible(true);
            } catch (Exception ex) { JOptionPane.showMessageDialog(dialog, ex.getMessage()); }
        });
        
        bookP.add(new JLabel("Check-In (YYYY-MM-DD):")); bookP.add(sDate);
        bookP.add(new JLabel("Check-Out (YYYY-MM-DD):")); bookP.add(eDate);
        bookP.add(checkBtn);
        bookP.add(new JLabel("Select Unit:")); bookP.add(unitBox);
        bookP.add(confirmBtn);

        // Reviews Tab
        JPanel revP = new JPanel(new BorderLayout());
        DefaultTableModel revModel = new DefaultTableModel(new String[]{"User", "Rating", "Comment"}, 0);
        JTable revTable = new JTable(revModel);
        for(Review r : place.getReviews()) revModel.addRow(new Object[]{r.getAuthorName(), r.getRating()+"/5", r.getComment()});
        
        JButton addRevBtn = StyleUtils.createOutlineButton("Add Review");
        addRevBtn.addActionListener(e -> {
            String comm = JOptionPane.showInputDialog("Comment:");
            if(comm != null) {
                String ratingStr = JOptionPane.showInputDialog("Rating (1-5):");
                try {
                    int r = Integer.parseInt(ratingStr);
                    controller.addReview(place, r, comm);
                    revModel.addRow(new Object[]{controller.getCurrentUser().getFullName(), r+"/5", comm});
                } catch(Exception ex) {}
            }
        });
        
        revP.add(new JScrollPane(revTable), BorderLayout.CENTER);
        revP.add(addRevBtn, BorderLayout.SOUTH);

        JTabbedPane dTabs = new JTabbedPane();
        dTabs.addTab("Info", infoP);
        dTabs.addTab("Book Now", bookP);
        dTabs.addTab("Reviews", revP);
        
        dialog.add(dTabs);
        dialog.setVisible(true);
    }

    private void loadPlaces() {
        model.setRowCount(0);
        List<Place_Base> list = controller.getApprovedPlaces();
        String txt = searchField.getText().toLowerCase();
        if(!txt.isEmpty()) {
            list.removeIf(p -> !p.getName().toLowerCase().contains(txt) && !p.getLocation().toLowerCase().contains(txt));
        }
        if(sortPrice.isSelected()) list.sort((a,b) -> Double.compare(a.getBasePrice(), b.getBasePrice()));

        for (Place_Base p : list) {
            model.addRow(new Object[]{p.getName(), p.getCategory(), p.getLocation(), "$" + p.getBasePrice(), p.getSpecialOffer()});
        }
    }
    
    private JPanel createBookingsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        String[] cols = {"ID", "Place", "Unit", "Dates", "Status", "Cost"};
        bookingsModel = new DefaultTableModel(cols, 0);
        bookingsTable = new JTable(bookingsModel);
        StyleUtils.styleTable(bookingsTable);
        
        JButton cancelBtn = StyleUtils.createStyledButton("Cancel Reservation");
        cancelBtn.addActionListener(e -> {
             int row = bookingsTable.getSelectedRow();
             if(row != -1) {
                 Reservation r = controller.getMyReservations().get(row);
                 controller.cancelReservation(r);
                 loadBookings();
                 JOptionPane.showMessageDialog(this, "Cancelled & Refunded.");
                 this.dispose(); new Client_Dashboard(controller).setVisible(true); // Refresh wallet
             }
        });
        
        p.add(new JScrollPane(bookingsTable), BorderLayout.CENTER);
        p.add(cancelBtn, BorderLayout.SOUTH);
        loadBookings();
        return p;
    }
    
    private void loadBookings() {
        bookingsModel.setRowCount(0);
        for(Reservation r : controller.getMyReservations()) {
            bookingsModel.addRow(new Object[]{r.getId(), r.getPlace().getName(), r.getUnitName(), r.getStartDate() + " -> " + r.getEndDate(), r.getStatus(), "$" + r.getTotalCost()});
        }
    }
}
