package View;

import Controller.App_Controller;
import Model.User_Base;
import javax.swing.*;
import java.awt.*;

public class Login_View extends JFrame {
    private App_Controller controller;
    private JTextField userField;
    private JPasswordField passField;
    private JComboBox<String> roleBox;

    public Login_View(App_Controller controller) {
        this.controller = controller;
        setTitle("Vacationly - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Username:"));
        userField = new JTextField();
        panel.add(userField);

        panel.add(new JLabel("Password:"));
        passField = new JPasswordField();
        panel.add(passField);

        panel.add(new JLabel("Role:"));
        roleBox = new JComboBox<>(new String[]{"Client", "Admin"});
        panel.add(roleBox);

        JButton loginBtn = new JButton("Login");
        JButton regBtn = new JButton("Register (Client)");

        loginBtn.addActionListener(e -> handleLogin());
        regBtn.addActionListener(e -> handleRegister());

        panel.add(loginBtn);
        panel.add(regBtn);

        add(panel);
    }

    private void handleLogin() {
        String u = userField.getText();
        String p = new String(passField.getPassword());
        boolean isAdmin = roleBox.getSelectedItem().equals("Admin");

        User_Base user = controller.login(u, p, isAdmin);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Welcome " + user.getFullName());
            this.dispose();
            if (isAdmin) {
                new Admin_Dashboard(controller).setVisible(true);
            } else {
                new Client_Dashboard(controller).setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister() {
        String u = userField.getText();
        String p = new String(passField.getPassword());
        if(u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter username and password");
            return;
        }
        controller.registerClient(u, p, u); // simple registration
        JOptionPane.showMessageDialog(this, "Registered! Please login.");
    }
}