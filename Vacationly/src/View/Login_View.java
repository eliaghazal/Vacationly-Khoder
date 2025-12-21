package View;

import Controller.App_Controller;
import Model.*;
import javax.swing.*;
import java.awt.*;

public class Login_View extends JFrame {
    private App_Controller controller;
    private JTextField userField;
    private JPasswordField passField;

    public Login_View(App_Controller controller) {
        this.controller = controller;
        setTitle("Vacationly - Welcome");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        StyleUtils.styleFrame(this);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("VACATIONLY", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(StyleUtils.PRIMARY);

        userField = new JTextField();
        userField.setBorder(BorderFactory.createTitledBorder("Username"));
        
        passField = new JPasswordField();
        passField.setBorder(BorderFactory.createTitledBorder("Password"));

        JButton loginBtn = StyleUtils.createStyledButton("Login");
        JButton regBtn = new JButton("Create New Account");
        regBtn.setBorderPainted(false);
        regBtn.setContentAreaFilled(false);
        regBtn.setForeground(Color.BLUE);
        regBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginBtn.addActionListener(e -> handleLogin());
        regBtn.addActionListener(e -> {
            new Register_View(controller).setVisible(true);
            this.dispose();
        });

        panel.add(title);
        panel.add(userField);
        panel.add(passField);
        panel.add(loginBtn);
        panel.add(new JSeparator());
        panel.add(regBtn);

        add(panel);
    }

    private void handleLogin() {
        String u = userField.getText();
        String p = new String(passField.getPassword());
        User_Base user = controller.login(u, p);
        
        if (user != null) {
            this.dispose();
            if (user instanceof Admin) new Admin_Dashboard(controller).setVisible(true);
            else if (user instanceof Client) new Client_Dashboard(controller).setVisible(true);
            else if (user instanceof BusinessOwner) new Owner_Dashboard(controller).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
