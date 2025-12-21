package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StyleUtils {
    public static final Color PRIMARY = new Color(70, 130, 180); // Steel Blue
    public static final Color BG_COLOR = new Color(245, 248, 250);
    public static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 18);
    public static final Font DATA_FONT = new Font("SansSerif", Font.PLAIN, 14);

    public static void styleFrame(JFrame frame) {
        frame.getContentPane().setBackground(BG_COLOR);
    }
    
    public static void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(DATA_FONT);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(Color.WHITE);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
    }

    public static JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        return btn;
    }
}
