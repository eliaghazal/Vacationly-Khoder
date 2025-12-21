package View;

import Controller.App_Controller;
import javax.swing.*;
import java.awt.*;

public class Register_View extends JFrame {
    private App_Controller controller;
    private JTextField nameField, userField, extraField; 
    private JPasswordField passField;
    private JComboBox<String> roleBox;

    public Register_View(App_Controller controller) {
        this.controller = controller;
        setTitle("Vacationly - Register");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        StyleUtils.styleFrame(this);
        
        JPanel panel = new JPanel(new GridLayout(7, 1, 10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        nameField = new JTextField(); nameField.setBorder(BorderFactory.createTitledBorder("Full Name"));
        userField = new JTextField(); userField.setBorder(BorderFactory.createTitledBorder("Username"));
        passField = new JPasswordField(); passField.setBorder(BorderFactory.createTitledBorder("Password"));
        
        roleBox = new JComboBox<>(new String[]{"Client", "Business Owner"});
        roleBox.setBorder(BorderFactory.createTitledBorder("Account Type"));
        
        extraField = new JTextField(); 
        extraField.setBorder(BorderFactory.createTitledBorder("Initial Deposit ($)")); // Default for Client
        
        roleBox.addActionListener(e -> {
            if(roleBox.getSelectedItem().equals("Client")) {
                extraField.setBorder(BorderFactory.createTitledBorder("Initial Deposit ($)"));
            } else {
                extraField.setBorder(BorderFactory.createTitledBorder("Contact Info"));
            }
        });

        JButton regBtn = StyleUtils.createStyledButton("Sign Up");
        JButton backBtn = new JButton("Back to Login");
        
        regBtn.addActionListener(e -> handleRegister());
        backBtn.addActionListener(e -> {
            new Login_View(controller).setVisible(true);
            this.dispose();
        });

        panel.add(new JLabel("Create Account", SwingConstants.CENTER));
        panel.add(nameField);
        panel.add(userField);
        panel.add(passField);
        panel.add(roleBox);
        panel.add(extraField);
        panel.add(regBtn);
        
        add(panel, BorderLayout.CENTER);
        add(backBtn, BorderLayout.SOUTH);
    }

    private void handleRegister() {
        try {
            String role = (String) roleBox.getSelectedItem();
            if(role.equals("Client")) {
                double bal = Double.parseDouble(extraField.getText());
                controller.registerClient(nameField.getText(), userField.getText(), new String(passField.getPassword()), bal, "0000-0000");
            } else {
                controller.registerOwner(nameField.getText(), userField.getText(), new String(passField.getPassword()), extraField.getText());
            }
            JOptionPane.showMessageDialog(this, "Registration Successful!");
            new Login_View(controller).setVisible(true);
            this.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
