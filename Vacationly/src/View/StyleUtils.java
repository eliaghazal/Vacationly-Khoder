package View;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class StyleUtils {
    public static final Color PRIMARY = new Color(63, 81, 181); // Indigo
    public static final Color ACCENT = new Color(255, 64, 129); // Pink
    public static final Color BG_COLOR = new Color(245, 245, 245);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font DATA_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public static void styleFrame(JFrame frame) {
        frame.getContentPane().setBackground(BG_COLOR);
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setFont(DATA_FONT);
        table.setGridColor(new Color(230,230,230));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setForeground(PRIMARY);
        table.setShowVerticalLines(false);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(String.class, centerRenderer);
    }

    public static JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    public static JButton createOutlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.WHITE);
        btn.setForeground(PRIMARY);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createLineBorder(PRIMARY, 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // --- NEW: Fix for Titled Input Fields ---
    public static JPanel createTitledField(String title, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // Modern Titled Border
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)), title);
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 12));
        border.setTitleColor(new Color(100, 100, 100));
        
        panel.setBorder(border);
        
        // Remove default border from field and add internal padding
        field.setBorder(new EmptyBorder(5, 5, 5, 5));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // If it's a text field, make it transparent to blend with panel if needed
        if(field instanceof JTextField) {
            ((JTextField) field).setOpaque(false);
        }
        
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }
}
