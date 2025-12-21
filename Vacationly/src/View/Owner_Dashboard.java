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
        setSize(1100, 650); 
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
        
        // --- Updated Form Panel with Helper ---
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField name = new JTextField(10); 
        JTextField loc = new JTextField(10); 
        JTextField price = new JTextField(6); 
        JTextField cap = new JTextField(5); 
        JTextField offer = new JTextField(10); 
        
        JComboBox<String> type = new JComboBox<>(new String[]{"Hotel", "Restaurant", "Resort"});
        JButton addBtn = StyleUtils.createStyledButton("Add");
        
        // Wrap everything
        form.add(StyleUtils.createTitledField("Name", name));
        form.add(StyleUtils.createTitledField("Location", loc));
        form.add(StyleUtils.createTitledField("Price", price));
        form.add(StyleUtils.createTitledField("Cap.", cap));
        form.add(StyleUtils.createTitledField("Offer", offer));
        form.add(StyleUtils.createTitledField("Type", type));
        form.add(addBtn);
        
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
                
                if(type.getSelectedItem().equals("Hotel")) place = new Hotel(pid, oid, name.getText(), "Desc", loc.getText(), pr, c);
                else if(type.getSelectedItem().equals("Restaurant")) place = new Restaurant(pid, oid, name.getText(), "Desc", loc.getText(), pr, c);
                else place = new Resort(pid, oid, name.getText(), "Desc", loc.getText(), pr, c);
                
                String offerText = offer.getText().trim();
                place.setSpecialOffer(offerText.isEmpty() ? "None" : offerText);
                
                controller.addPlace(place);
                
                model.addRow(new Object[]{place.getName(), "Pending", "$" + place.getBasePrice(), c, place.getSpecialOffer()});
                
                name.setText(""); loc.setText(""); price.setText(""); cap.setText(""); offer.setText("");
                JOptionPane.showMessageDialog(this, "Business Added!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid Input"); }
        });
        
        for(Place_Base pl : controller.getOwnerPlaces(controller.getCurrentUser().getId())) {
             model.addRow(new Object[]{pl.getName(), pl.isApproved() ? "Approved" : "Pending", "$" + pl.getBasePrice(), pl.getUnits().size(), pl.getSpecialOffer()});
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
