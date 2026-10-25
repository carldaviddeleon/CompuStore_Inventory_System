package gui;

import javax.swing.*;
import java.awt.*;


public class Style {

    public static final Color DARK = new Color(15, 23, 42);         
    public static final Color BLUE = new Color(37, 99, 235);         
    public static final Color GRAY = new Color(100, 116, 139);
    public static final Color RED = new Color(220, 38, 38);
    public static final Color BACKGROUND = new Color(248, 250, 252);

    public static JButton makeButton(String text, char key, String tip, Color color){
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setMargin(new Insets(8, 16, 8, 16));
        button.setMnemonic(key);
        button.setToolTipText(tip);
        return button;
    }

    public static JButton makeSidebarButton(String text, char key, String tip){
        JButton button = makeButton(text, key, tip, DARK);
        button.setHorizontalAlignment(JButton.LEFT);
        button.setMargin(new Insets(12, 25, 12, 10));
        return button;
    }

    public static JTextField makeField(String title, String tip){
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createTitledBorder(title));
        field.setToolTipText(tip);
        return field;
    }

    public static JPasswordField makePasswordField(String title, String tip){
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createTitledBorder(title));
        field.setToolTipText(tip);
        return field;
    }

    public static JLabel makeLogo(){
        JLabel logo = new JLabel(new ImageIcon("logo.png"));
        logo.setHorizontalAlignment(JLabel.CENTER);
        return logo;
    }

    public static void styleTable(JTable table){
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);              
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setDefaultEditor(Object.class, null);      
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setBackground(DARK);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
    }

    public static void warn(Component parent, String message){
        JOptionPane.showMessageDialog(parent, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public static void info(Component parent, String message){
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}
