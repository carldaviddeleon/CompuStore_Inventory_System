package gui;

import manager.UserManager;
import model.Staff;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SignUpFrame extends JFrame implements ActionListener {

    private UserManager userManager;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirm;
    private JButton btnCreate;
    private JButton btnBack;

    public SignUpFrame(){
        userManager = new UserManager();
        setupWindow();
        add(makeHeaderPanel(), BorderLayout.NORTH);
        add(makeCenterPanel(), BorderLayout.CENTER);
        btnCreate.addActionListener(this);
        btnBack.addActionListener(this);
        getRootPane().setDefaultButton(btnCreate);
    }

    private void setupWindow(){
        setTitle("CompuStore - Sign Up");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setExtendedState(MAXIMIZED_BOTH);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    private JPanel makeHeaderPanel(){
        JLabel lblTitle = new JLabel("CompuStore Inventory System", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Style.DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        panel.add(Style.makeLogo(), BorderLayout.NORTH);
        panel.add(lblTitle, BorderLayout.CENTER);
        return panel;
    }

    private JPanel makeFormPanel(){
        txtUsername = Style.makeField("Username *", "Letters and numbers only, no spaces");
        txtPassword = Style.makePasswordField("Password *", "Minimum 6 characters");
        txtConfirm = Style.makePasswordField("Confirm Password *", "Type the same password again");
        btnCreate = Style.makeButton("Create Account", 'C', "Click to create the account (Alt+C)", Style.BLUE);
        btnBack = Style.makeButton("Back to Login", 'B', "Go back to the login screen (Alt+B)", Style.GRAY);

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.add(new JLabel("Create a new account (STAFF - view-only)", JLabel.CENTER));
        panel.add(txtUsername);
        panel.add(txtPassword);
        panel.add(txtConfirm);
        panel.add(btnCreate);
        panel.add(btnBack);
        panel.add(new JLabel("* = required field"));
        return panel;
    }

    private JPanel makeCenterPanel(){
        JPanel form = makeFormPanel();
        form.setPreferredSize(new Dimension(420, 470));
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)));

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(Style.BACKGROUND);
        center.add(form);
        return center;
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnCreate) doSignUp();
        else if(e.getSource() == btnBack) goBackToLogin();
    }

    private void doSignUp(){
        if(!validateInputs()) return;

        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        Staff newUser = new Staff(username, userManager.makeHash(password));

        if(userManager.addUser(newUser)){
            Style.info(this, "Account created successfully. You can now login.");
            goBackToLogin();
        }else{
            Style.warn(this, "The account could not be saved");
        }
    }

    private boolean validateInputs(){
        if(!checkRequiredFields()) return false;
        if(!checkUsername()) return false;
        if(!checkPasswords()) return false;
        return true;
    }

    private boolean checkRequiredFields(){
        String password = new String(txtPassword.getPassword());
        String confirm = new String(txtConfirm.getPassword());
        if(txtUsername.getText().trim().isEmpty() || password.isEmpty() || confirm.isEmpty()){
            Style.warn(this, "Please fill in all required fields");
            return false;
        }
        return true;
    }

    private boolean checkUsername(){
        String username = txtUsername.getText().trim();
        if(username.contains(" ") || username.contains(",")){
            Style.warn(this, "Username cannot contain spaces or commas");
            return false;
        }
        if(userManager.isDuplicateUsername(username)){
            Style.warn(this, "Username already exists");
            return false;
        }
        return true;
    }

    private boolean checkPasswords(){
        String password = new String(txtPassword.getPassword());
        String confirm = new String(txtConfirm.getPassword());
        if(password.length() < 6){
            Style.warn(this, "Password must be at least 6 characters");
            return false;
        }
        if(!password.equals(confirm)){
            Style.warn(this, "Passwords do not match");
            return false;
        }
        return true;
    }

    private void goBackToLogin(){
        new LoginFrame().setVisible(true);
        dispose();
    }
}