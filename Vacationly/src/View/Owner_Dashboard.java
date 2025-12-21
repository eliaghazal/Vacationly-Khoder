package View;

import Controller.App_Controller;
import Model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Owner_Dashboard extends JFrame {
    private App_Controller controller;

    public Owner_Dashboard(App_Controller controller) {
        this.controller = controller;
        setTitle("Vacationly - Business Manager");
        setSize(1100, 600); // Widened slightly to fit the new column
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        StyleUtils.styleFrame(this);
        
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(StyleUtils.HEADER_FONT);
        tabs.addTab("My Businesses", createBusinessPanel());
        tabs.addTab("Reservations", createResPanel());
        tabs.addTab("Messages", createMsgPanel());
        
        JButton logout = StyleUtils.createOutlineButton("Logout");
        logout.addActionListener(e -> { new Login_View(controller).setVisible(true); dispose(); });
        
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        header.add(new JLabel("Owner Portal"), BorderLayout.WEST);
        header.add(logout, BorderLayout.EAST);
        
        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }
    
    private JPanel createBusinessPanel() {
        JPanel p = new JPanel(new BorderLayout());
        
        // --- Updated Form Panel ---
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JTextField name = new JTextField(10); name.setBorder(BorderFactory.createTitledBorder("Name"));
        JTextField loc = new JTextField(10); loc.setBorder(BorderFactory.createTitledBorder("Location"));
        JTextField price = new JTextField(6); price.setBorder(BorderFactory.createTitledBorder("Price ($)"));
        JTextField cap = new JTextField(5); cap.setBorder(BorderFactory.createTitledBorder("Cap."));
        
        // NEW: Offer Field
        JTextField offer = new JTextField(10); offer.setBorder(BorderFactory.createTitledBorder("Special Offer"));
        
        JComboBox<String> type = new JComboBox<>(new String[]{"Hotel", "Restaurant", "Resort"});
        JButton addBtn = StyleUtils.createStyledButton("Add Business");
        
        form.add(name); form.add(loc); form.add(price); form.add(cap); 
        form.add(offer); // Added to layout
        form.add(type); form.add(addBtn);
        
        // --- Updated Table Model ---
        // Added "Offer" column
        DefaultTableModel model = new DefaultTableModel(new String[]{"Name", "Status", "Price", "Units", "Offer"}, 0);
        JTable table = new JTable(model);
        StyleUtils.styleTable(table);
        
        addBtn.addActionListener(e -> {
            try {
                String pid = "P" + System.currentTimeMillis();
                double pr = Double.parseDouble(price.getText());
                int c = Integer.parseInt(cap.getText());
                Place_Base place = null;
                String oid = controller.getCurrentUser().getId();
                
                // Create specific object
                if(type.getSelectedItem().equals("Hotel")) 
                    place = new Hotel(pid, oid, name.getText(), "Desc", loc.getText(), pr, c);
                else if(type.getSelectedItem().equals("Restaurant")) 
                    place = new Restaurant(pid, oid, name.getText(), "Desc", loc.getText(), pr, c);
                else 
                    place = new Resort(pid, oid, name.getText(), "Desc", loc.getText(), pr, c);
                
                // Set the Offer if provided
                String offerText = offer.getText().trim();
                if(!offerText.isEmpty()) {
                    place.setSpecialOffer(offerText);
                } else {
                    place.setSpecialOffer("None");
                }
                
                controller.addPlace(place);
                
                // Add to table
                model.addRow(new Object[]{
                    place.getName(), 
                    "Pending", 
                    "$" + place.getBasePrice(), 
                    c, 
                    place.getSpecialOffer() // Show in table
                });
                
                // Clear inputs
                name.setText(""); loc.setText(""); price.setText(""); cap.setText(""); offer.setText("");
                
                JOptionPane.showMessageDialog(this, "Business Added!");
                
            } catch (Exception ex) { 
                JOptionPane.showMessageDialog(this, "Invalid Input. Check numbers."); 
            }
        });
        
        // Load existing data
        for(Place_Base pl : controller.getOwnerPlaces(controller.getCurrentUser().getId())) {
             model.addRow(new Object[]{
                 pl.getName(), 
                 pl.isApproved() ? "Approved" : "Pending", 
                 "$" + pl.getBasePrice(), 
                 pl.getUnits().size(),
                 pl.getSpecialOffer() // Load existing offer
             });
        }
        
        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel createResPanel() {
        JPanel p = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Place", "Unit", "Date", "Status"}, 0);
        JTable table = new JTable(model);
        StyleUtils.styleTable(table);
        
        JButton cancelBtn = StyleUtils.createOutlineButton("Cancel Reservation");
        cancelBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row != -1) {
                Reservation r = controller.getMyReservations().get(row);
                controller.cancelReservation(r);
                loadRes(model);
            }
        });
        
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(cancelBtn, BorderLayout.SOUTH);
        loadRes(model);
        return p;
    }
    
    private void loadRes(DefaultTableModel m) {
        m.setRowCount(0);
        for(Reservation r : controller.getMyReservations()) {
            m.addRow(new Object[]{r.getId(), r.getPlace().getName(), r.getUnitName(), r.getStartDate(), r.getStatus()});
        }
    }
    
    private JPanel createMsgPanel() {
        JPanel p = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{"From", "Message", "My Reply"}, 0);
        JTable table = new JTable(model);
        StyleUtils.styleTable(table);
        
        JButton replyBtn = StyleUtils.createStyledButton("Reply");
        replyBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row != -1) {
                Message m = controller.getMyMessages().get(row);
                String rep = JOptionPane.showInputDialog("Reply:");
                if(rep != null) {
                    controller.replyMessage(m, rep);
                    loadMsg(model);
                }
            }
        });
        
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(replyBtn, BorderLayout.SOUTH);
        loadMsg(model);
        return p;
    }
    
    private void loadMsg(DefaultTableModel m) {
        m.setRowCount(0);
        for(Message msg : controller.getMyMessages()) {
            m.addRow(new Object[]{msg.getSenderName(), msg.getContent(), msg.getResponse()});
        }
    }
}
