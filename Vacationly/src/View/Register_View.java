package View;

import Controller.App_Controller;
import javax.swing.*;
import java.awt.*;

public class Register_View extends JFrame {
    private App_Controller controller;
    private JTextField nameField, userField, extraField; 
    private JPasswordField passField;
    private JComboBox<String> roleBox;
    private JPanel extraPanel; // To hold the dynamic titled field

    public Register_View(App_Controller controller) {
        this.controller = controller;
        setTitle("Vacationly - Register");
        setSize(420, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        StyleUtils.styleFrame(this);
        
        JPanel panel = new JPanel(new GridLayout(7, 1, 15, 15));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        nameField = new JTextField();
        userField = new JTextField();
        passField = new JPasswordField();
        extraField = new JTextField();
        
        roleBox = new JComboBox<>(new String[]{"Client", "Business Owner"});
        
        // Dynamic Panel for Extra Field (Balance vs Contact)
        extraPanel = new JPanel(new BorderLayout());
        extraPanel.setOpaque(false);
        updateExtraField("Client"); // Default
        
        roleBox.addActionListener(e -> updateExtraField((String)roleBox.getSelectedItem()));

        // Wrap inputs
        JPanel nameP = StyleUtils.createTitledField("Full Name", nameField);
        JPanel userP = StyleUtils.createTitledField("Username", userField);
        JPanel passP = StyleUtils.createTitledField("Password", passField);
        JPanel roleP = StyleUtils.createTitledField("Account Type", roleBox);

        JButton regBtn = StyleUtils.createStyledButton("Sign Up");
        JButton backBtn = new JButton("Back to Login");
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setForeground(StyleUtils.PRIMARY);
        
        regBtn.addActionListener(e -> handleRegister());
        backBtn.addActionListener(e -> {
            new Login_View(controller).setVisible(true);
            this.dispose();
        });

        JLabel head = new JLabel("Create Account", SwingConstants.CENTER);
        head.setFont(StyleUtils.HEADER_FONT);

        panel.add(head);
        panel.add(nameP);
        panel.add(userP);
        panel.add(passP);
        panel.add(roleP);
        panel.add(extraPanel); // Dynamic
        panel.add(regBtn);
        
        add(panel, BorderLayout.CENTER);
        add(backBtn, BorderLayout.SOUTH);
    }
    
    private void updateExtraField(String role) {
        extraPanel.removeAll();
        String title = role.equals("Client") ? "Initial Deposit ($)" : "Contact Info";
        extraPanel.add(StyleUtils.createTitledField(title, extraField));
        extraPanel.revalidate();
        extraPanel.repaint();
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
