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
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JCheckBox sortPrice;

    public Client_Dashboard(App_Controller controller) {
        this.controller = controller;
        setTitle("Vacationly - Client Portal");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        StyleUtils.styleFrame(this);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Browse Businesses", createBrowsePanel());
        tabs.addTab("My Bookings", createBookingsPanel());
        
        Client client = (Client) controller.getCurrentUser();
        JLabel header = new JLabel("  Wallet: $" + client.getBalance() + " | User: " + client.getFullName());
        header.setFont(StyleUtils.HEADER_FONT);
        header.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
             new Login_View(controller).setVisible(true);
             dispose();
        });

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(header, BorderLayout.CENTER);
        top.add(logout, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createBrowsePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        
        // Filter Bar
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(15);
        sortPrice = new JCheckBox("Sort Cheapest First");
        JButton searchBtn = StyleUtils.createStyledButton("Search");
        searchBtn.addActionListener(e -> loadPlaces());
        
        filters.add(new JLabel("Search:"));
        filters.add(searchField);
        filters.add(sortPrice);
        filters.add(searchBtn);
        
        // Table
        String[] cols = {"Name", "Category", "Location", "Price/Night", "Offer"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        StyleUtils.styleTable(table);
        
        // Booking Area
        JPanel bookPanel = new JPanel();
        JTextField startF = new JTextField("YYYY-MM-DD", 8);
        JTextField endF = new JTextField("YYYY-MM-DD", 8);
        JButton bookBtn = StyleUtils.createStyledButton("Book Selected");
        
        bookBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row == -1) {
                JOptionPane.showMessageDialog(this, "Select a place first!");
                return;
            }
            try {
                LocalDate s = LocalDate.parse(startF.getText());
                LocalDate en = LocalDate.parse(endF.getText());
                // Get the place object (In real app, maintain a list mapping rows to IDs)
                String name = (String) model.getValueAt(row, 0);
                Place_Base place = controller.getApprovedPlaces().stream()
                        .filter(p -> p.getName().equals(name)).findFirst().orElse(null);
                
                controller.createReservation(place, s, en);
                JOptionPane.showMessageDialog(this, "Booking Confirmed!");
                // Refresh to update balance display
                dispose();
                new Client_Dashboard(controller).setVisible(true);
                
            } catch (DateTimeParseException dt) {
                JOptionPane.showMessageDialog(this, "Invalid Date Format (YYYY-MM-DD)");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Booking Failed: " + ex.getMessage());
            }
        });
        
        bookPanel.add(new JLabel("Check-in:")); bookPanel.add(startF);
        bookPanel.add(new JLabel("Check-out:")); bookPanel.add(endF);
        bookPanel.add(bookBtn);

        panel.add(filters, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(bookPanel, BorderLayout.SOUTH);
        
        loadPlaces();
        return panel;
    }

    private void loadPlaces() {
        model.setRowCount(0);
        List<Place_Base> list = controller.getApprovedPlaces();
        
        // Filter
        String txt = searchField.getText().toLowerCase();
        if(!txt.isEmpty()) {
            list.removeIf(p -> !p.getName().toLowerCase().contains(txt) && !p.getLocation().toLowerCase().contains(txt));
        }
        
        // Sort
        if(sortPrice.isSelected()) {
            list.sort((a,b) -> Double.compare(a.getBasePrice(), b.getBasePrice()));
        }

        for (Place_Base p : list) {
            model.addRow(new Object[]{p.getName(), p.getCategory(), p.getLocation(), "$" + p.getBasePrice(), p.getSpecialOffer()});
        }
    }
    
    private JPanel createBookingsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        String[] cols = {"ID", "Place", "Dates", "Status", "Cost"};
        DefaultTableModel mod = new DefaultTableModel(cols, 0);
        JTable tbl = new JTable(mod);
        StyleUtils.styleTable(tbl);
        
        for(Reservation r : controller.getMyReservations()) {
            mod.addRow(new Object[]{r.getId(), r.getPlace().getName(), r.getStartDate() + " to " + r.getEndDate(), r.getStatus(), "$" + r.getTotalCost()});
        }
        
        p.add(new JScrollPane(tbl), BorderLayout.CENTER);
        return p;
    }
}
