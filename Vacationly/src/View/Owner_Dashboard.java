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
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        StyleUtils.styleFrame(this);
        
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("My Businesses", createBusinessPanel());
        tabs.addTab("Incoming Reservations", createResPanel());
        
        add(tabs, BorderLayout.CENTER);
        
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> { new Login_View(controller).setVisible(true); dispose(); });
        add(logout, BorderLayout.NORTH);
    }
    
    private JPanel createBusinessPanel() {
        JPanel p = new JPanel(new BorderLayout());
        
        // Form to add business
        JPanel form = new JPanel(new GridLayout(1, 5));
        JTextField name = new JTextField(); name.setBorder(BorderFactory.createTitledBorder("Name"));
        JTextField loc = new JTextField(); loc.setBorder(BorderFactory.createTitledBorder("Location"));
        JTextField price = new JTextField(); price.setBorder(BorderFactory.createTitledBorder("Price"));
        JComboBox<String> type = new JComboBox<>(new String[]{"Hotel", "Restaurant", "Resort"});
        JButton addBtn = StyleUtils.createStyledButton("Add Business");
        
        form.add(name); form.add(loc); form.add(price); form.add(type); form.add(addBtn);
        
        // Table
        DefaultTableModel model = new DefaultTableModel(new String[]{"Name", "Status", "Price"}, 0);
        JTable table = new JTable(model);
        StyleUtils.styleTable(table);
        
        addBtn.addActionListener(e -> {
            try {
                String oid = controller.getCurrentUser().getId();
                String pid = "P" + System.currentTimeMillis();
                double pr = Double.parseDouble(price.getText());
                Place_Base place = null;
                
                if(type.getSelectedItem().equals("Hotel")) 
                    place = new Hotel(pid, oid, name.getText(), "Desc", loc.getText(), pr, 10);
                else if(type.getSelectedItem().equals("Restaurant")) 
                    place = new Restaurant(pid, oid, name.getText(), "Desc", loc.getText(), pr, 10);
                else 
                    place = new Resort(pid, oid, name.getText(), "Desc", loc.getText(), pr, 10);
                
                controller.addPlace(place);
                JOptionPane.showMessageDialog(this, "Business Added! Waiting for Admin Approval.");
                // Refresh table logic here...
                model.addRow(new Object[]{place.getName(), "Pending", "$" + place.getBasePrice()});
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid Input");
            }
        });
        
        // Load initial
        for(Place_Base pl : controller.getOwnerPlaces(controller.getCurrentUser().getId())) {
             model.addRow(new Object[]{pl.getName(), pl.isApproved() ? "Approved" : "Pending", "$" + pl.getBasePrice()});
        }
        
        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel createResPanel() {
        JPanel p = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{"Res ID", "Client", "Place", "Date", "Status"}, 0);
        JTable table = new JTable(model);
        StyleUtils.styleTable(table);
        
        for(Reservation r : controller.getMyReservations()) {
            model.addRow(new Object[]{r.getId(), r.getClientId(), r.getPlace().getName(), r.getStartDate(), r.getStatus()});
        }
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }
}
