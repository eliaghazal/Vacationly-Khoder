package View;

import Controller.App_Controller;
import Model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Admin_Dashboard extends JFrame {
    private App_Controller controller;

    public Admin_Dashboard(App_Controller controller) {
        this.controller = controller;
        setTitle("Vacationly - Administrator");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        StyleUtils.styleFrame(this);

        // --- Header ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel title = new JLabel("ADMIN PANEL");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(StyleUtils.PRIMARY);
        
        JButton logout = StyleUtils.createOutlineButton("Logout");
        logout.addActionListener(e -> {
            new Login_View(controller).setVisible(true);
            this.dispose();
        });
        
        header.add(title, BorderLayout.WEST);
        header.add(logout, BorderLayout.EAST);

        // --- Tabs ---
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(StyleUtils.HEADER_FONT);
        tabs.addTab("Manage Businesses", createPlacesPanel());
        tabs.addTab("All Reservations", createResPanel());
        tabs.addTab("Moderation (Reviews)", createReviewPanel());

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    // --- Tab 1: Approve/Ban Businesses ---
    private JPanel createPlacesPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[] cols = {"ID", "Name", "Owner", "Status", "Action"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        StyleUtils.styleTable(table);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        
        JButton approveBtn = StyleUtils.createStyledButton("Approve Selected");
        JButton banBtn = StyleUtils.createOutlineButton("Ban / Revoke");
        
        approveBtn.addActionListener(e -> updateStatus(table, true));
        banBtn.addActionListener(e -> updateStatus(table, false));
        
        btnPanel.add(approveBtn);
        btnPanel.add(banBtn);
        
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(btnPanel, BorderLayout.SOUTH);
        
        loadPlaces(model);
        return p;
    }
    
    private void updateStatus(JTable table, boolean status) {
        int row = table.getSelectedRow();
        if(row != -1) {
            String id = (String) table.getModel().getValueAt(row, 0);
            Place_Base place = controller.getAllPlaces().stream()
                    .filter(pl -> pl.getId().equals(id)).findFirst().orElse(null);
            
            if(place != null) {
                place.setApproved(status);
                // In a real app, you would call controller.updatePlace(place) to save
                JOptionPane.showMessageDialog(this, "Status updated to: " + (status ? "Approved" : "Banned"));
                
                // Refresh
                DefaultTableModel m = (DefaultTableModel) table.getModel();
                m.setValueAt(status ? "Approved" : "Pending/Banned", row, 3);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select a business first.");
        }
    }

    private void loadPlaces(DefaultTableModel model) {
        model.setRowCount(0);
        for(Place_Base p : controller.getAllPlaces()) {
            model.addRow(new Object[]{
                p.getId(), 
                p.getName(), 
                p.getOwnerId(), 
                p.isApproved() ? "Approved" : "Pending/Banned", 
                "Select to Edit"
            });
        }
    }

    // --- Tab 2: View All Reservations ---
    private JPanel createResPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Client", "Place", "Unit", "Dates", "Status"}, 0);
        JTable table = new JTable(model);
        StyleUtils.styleTable(table);
        
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Load Data
        for(Reservation r : controller.getAllReservations()) {
            model.addRow(new Object[]{
                r.getId(), 
                r.getClientId(), 
                r.getPlace().getName(), 
                r.getUnitName(),
                r.getStartDate() + " -> " + r.getEndDate(), 
                r.getStatus() 
            });
        }
        return p;
    }

    // --- Tab 3: Review Moderation ---
    private JPanel createReviewPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Note: We need to flatten the list of reviews to display them in a table
        String[] cols = {"Place", "User", "Rating", "Comment"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        StyleUtils.styleTable(table);
        
        JButton deleteBtn = StyleUtils.createOutlineButton("Remove Fraudulent Review");
        
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row != -1) {
                String placeName = (String) model.getValueAt(row, 0);
                String comment = (String) model.getValueAt(row, 3);
                
                // Find and remove
                Place_Base place = controller.getAllPlaces().stream()
                        .filter(pl -> pl.getName().equals(placeName)).findFirst().orElse(null);
                
                if(place != null) {
                    place.getReviews().removeIf(rev -> rev.getComment().equals(comment));
                    JOptionPane.showMessageDialog(this, "Review Removed.");
                    loadReviews(model); // Refresh
                }
            }
        });
        
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(deleteBtn, BorderLayout.SOUTH);
        
        loadReviews(model);
        return p;
    }
    
    private void loadReviews(DefaultTableModel model) {
        model.setRowCount(0);
        for(Place_Base p : controller.getAllPlaces()) {
            for(Review r : p.getReviews()) {
                model.addRow(new Object[]{
                    p.getName(),
                    r.getAuthorName(),
                    r.getRating() + "/5",
                    r.getComment()
                });
            }
        }
    }
}
