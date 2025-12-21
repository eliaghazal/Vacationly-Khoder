package View;

import Controller.App_Controller;
import Model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Admin_Dashboard extends JFrame {
    private App_Controller controller;
    private JTable resTable, placesTable, msgTable;
    private DefaultTableModel resModel, placesModel, msgModel;

    public Admin_Dashboard(App_Controller controller) {
        this.controller = controller;
        setTitle("Vacationly - Admin Panel");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Manage Reservations", createResPanel());
        tabs.addTab("Manage Places", createPlacesPanel());
        tabs.addTab("Messages", createMsgPanel());
        
        // Add Logout
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            controller.logout();
            this.dispose();
            new Login_View(controller).setVisible(true);
        });
        topPanel.add(new JLabel("Admin Mode"));
        topPanel.add(logoutBtn);
        
        add(topPanel, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }
    
    private JPanel createResPanel() {
        JPanel p = new JPanel(new BorderLayout());
        String[] cols = {"Res ID", "Client ID", "Place", "Date", "Cost", "Status"};
        resModel = new DefaultTableModel(cols, 0);
        resTable = new JTable(resModel);
        
        JPanel btnPanel = new JPanel();
        JButton cancelBtn = new JButton("Cancel Selected");
        
        cancelBtn.addActionListener(e -> {
            int row = resTable.getSelectedRow();
            if(row != -1) {
                // Safe check for list bounds
                java.util.List<Reservation> allRes = controller.getAllReservations();
                if (row < allRes.size()) {
                    Reservation r = allRes.get(row);
                    controller.updateReservationStatus(r, false); // Cancel
                    loadReservations();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a reservation first.");
            }
        });
        
        btnPanel.add(cancelBtn);
        
        p.add(new JScrollPane(resTable), BorderLayout.CENTER);
        p.add(btnPanel, BorderLayout.SOUTH);
        
        loadReservations();
        return p;
    }
    
    private void loadReservations() {
        resModel.setRowCount(0);
        for(Reservation r : controller.getAllReservations()) {
            resModel.addRow(new Object[]{
                r.getId(), 
                r.getClientId(), 
                r.getPlace().getName(), 
                r.getDate(), 
                r.getTotalCost(), 
                r.getStatus() 
            });
        }
    }

    private JPanel createPlacesPanel() {
        JPanel p = new JPanel(new BorderLayout());
        placesModel = new DefaultTableModel(new String[]{"ID", "Name", "Price", "Trending", "Offer"}, 0);
        placesTable = new JTable(placesModel);
        
        p.add(new JScrollPane(placesTable), BorderLayout.CENTER);
        loadPlaces();
        return p;
    }
    
    private void loadPlaces() {
        placesModel.setRowCount(0);
        for(Place_Base p : controller.getAllPlaces()) {
            placesModel.addRow(new Object[]{
                p.getId(), 
                p.getName(), 
                p.getBasePrice(), 
                p.isTrending(), 
                p.getSpecialOffer()
            });
        }
    }
    
    private JPanel createMsgPanel() {
        JPanel p = new JPanel(new BorderLayout());
        msgModel = new DefaultTableModel(new String[]{"Sender", "Content", "Response", "Status"}, 0);
        msgTable = new JTable(msgModel);
        
        JButton replyBtn = new JButton("Reply");
        replyBtn.addActionListener(e -> {
            int row = msgTable.getSelectedRow();
            if(row != -1) {
                Message m = controller.getMessages().get(row);
                String resp = JOptionPane.showInputDialog("Enter response:");
                if(resp != null) {
                    controller.respondToMessage(m, resp);
                    loadMessages();
                }
            }
        });
        
        p.add(new JScrollPane(msgTable), BorderLayout.CENTER);
        p.add(replyBtn, BorderLayout.SOUTH);
        loadMessages();
        return p;
    }
    
    private void loadMessages() {
        msgModel.setRowCount(0);
        for(Message m : controller.getMessages()) {
            msgModel.addRow(new Object[]{
                m.getSenderName(), 
                m.getContent(), 
                m.getResponse(), 
                m.isResolved() ? "Resolved" : "Open"
            });
        }
    }
}