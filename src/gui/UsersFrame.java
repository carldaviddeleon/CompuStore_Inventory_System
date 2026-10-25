package gui;

import manager.UserManager;
import model.Admin;
import model.Staff;
import model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


public class UsersFrame extends JFrame implements ActionListener {

    private UserManager userManager;
    private User currentUser;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRole;
    private JButton btnAdd;
    private JButton btnUnblock;
    private JButton btnClose;
    private JTable table;
    private DefaultTableModel tableModel;

    public UsersFrame(UserManager userManager, User user){
        this.userManager = userManager;
        this.currentUser = user;
        setupWindow();
        add(makeTopPanel(), BorderLayout.NORTH);
        add(makeTablePanel(), BorderLayout.CENTER);
        btnAdd.addActionListener(this);
        btnUnblock.addActionListener(this);
        btnClose.addActionListener(this);
        loadTable();
    }

    private void setupWindow(){
        setTitle("CompuStore - Manage Users");
        setSize(800, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Style.BACKGROUND);
    }

    private JPanel makeTopPanel(){
        txtUsername = Style.makeField("Username *", "Letters and numbers only, no spaces");
        txtPassword = Style.makePasswordField("Password *", "Minimum 6 characters");
        cbRole = new JComboBox<String>(new String[]{"STAFF", "ADMIN"});
        cbRole.setBorder(BorderFactory.createTitledBorder("Access Level *"));
        cbRole.setToolTipText("ADMIN = full access, STAFF = view-only");
        btnAdd = Style.makeButton("Add User", 'A', "Click to add the new user (Alt+A)", Style.BLUE);
        btnUnblock = Style.makeButton("Unblock", 'U', "Click to unblock the selected user (Alt+U)", Style.BLUE);
        btnClose = Style.makeButton("Close", 'C', "Click to close this screen (Alt+C)", Style.GRAY);

        JPanel panel = new JPanel(new GridLayout(2, 3, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.add(txtUsername);
        panel.add(txtPassword);
        panel.add(cbRole);
        panel.add(btnAdd);
        panel.add(btnUnblock);
        panel.add(btnClose);
        return panel;
    }

    private JPanel makeTablePanel(){
        tableModel = new DefaultTableModel(new String[]{"Username", "Access Level", "Status"}, 0);
        table = new JTable(tableModel);
        Style.styleTable(table);
        table.setToolTipText("Select a blocked user, then click Unblock");

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Style.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnAdd) addUser();
        else if(e.getSource() == btnUnblock) unblockUser();
        else if(e.getSource() == btnClose) dispose();
    }

    private void loadTable(){
        tableModel.setRowCount(0);
        ArrayList<User> list = userManager.getAllUsers();
        for(User u : list){
            String status = "Active";
            if(u.isBlocked()) status = "Blocked";
            tableModel.addRow(new Object[]{u.getUsername(), u.getRole(), status});
        }
    }

    private boolean validateInputs(){
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if(username.isEmpty() || password.isEmpty()){
            Style.warn(this, "Please fill in all required fields");
            return false;
        }
        if(username.contains(" ") || username.contains(",")){
            Style.warn(this, "Username cannot contain spaces or commas");
            return false;
        }
        return checkNewUser(username, password);
    }

    private boolean checkNewUser(String username, String password){
        if(userManager.isDuplicateUsername(username)){
            Style.warn(this, "Username already exists");
            return false;
        }
        if(password.length() < 6){
            Style.warn(this, "Password must be at least 6 characters");
            return false;
        }
        return true;
    }

    private void addUser(){
        if(!validateInputs()) return;

        String username = txtUsername.getText().trim();
        String hash = userManager.makeHash(new String(txtPassword.getPassword()));
        User newUser;
        if(cbRole.getSelectedItem().toString().equals("ADMIN")){
            newUser = new Admin(username, hash);
        }else{
            newUser = new Staff(username, hash);
        }

        if(userManager.addUser(newUser)){
            Style.info(this, "User added successfully");
            txtUsername.setText("");
            txtPassword.setText("");
            loadTable();
        }else{
            Style.warn(this, "The user could not be saved");
        }
    }

    private void unblockUser(){
        int row = table.getSelectedRow();
        if(row == -1){
            Style.warn(this, "Please select a user from the table");
            return;
        }
        User selected = userManager.findUser(tableModel.getValueAt(row, 0).toString());
        if(selected == null || !selected.isBlocked()){
            Style.info(this, "This user is not blocked");
            return;
        }
        userManager.unblockUser(selected);
        Style.info(this, "User " + selected.getUsername() + " is unblocked");
        loadTable();
    }
}
