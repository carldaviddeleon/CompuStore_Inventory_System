package gui;

import manager.InventoryManager;
import manager.UserManager;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class LoginFrame extends JFrame implements ActionListener {

    private static int attempt = 0;

    private UserManager userManager;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnSignUp;
    
    public LoginFrame(){
        userManager = new UserManager();
        setupWindow();
        add(makeHeaderPanel(), BorderLayout.NORTH);
        add(makeCenterPanel(), BorderLayout.CENTER);
        btnLogin.addActionListener(this);
        btnSignUp.addActionListener(this);
        getRootPane().setDefaultButton(btnLogin);
    }

    private void setupWindow(){
        setTitle("CompuStore - Login");
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
        txtUsername = Style.makeField("Username *", "Enter your username");
        txtPassword = Style.makePasswordField("Password *", "Enter your password");
        btnLogin = Style.makeButton("Login", 'L', "Click to login (Alt+L or Enter)", Style.BLUE);
        btnSignUp = Style.makeButton("Sign Up", 'S', "Click to create a new account (Alt+S)", Style.GRAY);

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.add(txtUsername);
        panel.add(txtPassword);
        panel.add(btnLogin);
        panel.add(btnSignUp);
        panel.add(new JLabel("* = required field"));
        return panel;
    }

    private JPanel makeCenterPanel(){
        JPanel form = makeFormPanel();
        form.setPreferredSize(new Dimension(420, 360));
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)));

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(Style.BACKGROUND);
        center.add(form);
        return center;
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnLogin) doLogin();
        else if(e.getSource() == btnSignUp) openSignUp();
    }

    private void doLogin(){
        if(!validateInputs()) return;

        String password = new String(txtPassword.getPassword());
        User user = userManager.findUser(txtUsername.getText().trim());

        if(user != null && user.isBlocked()){
            Style.warn(this, "This account is blocked. Please ask the admin to unblock it.");
        }else if(user != null && userManager.checkPassword(user, password)){
            openDashboard(user);
        }else{
            wrongLogin(user);
        }
    }

    private boolean validateInputs(){
        String password = new String(txtPassword.getPassword());
        if(txtUsername.getText().trim().isEmpty() || password.isEmpty()){
            Style.warn(this, "Please enter your username and password");
            return false;
        }
        return true;
    }

    private void wrongLogin(User user){
        attempt++;
        if(attempt >= 3){
            blockAndExit(user);
        }else{
            Style.warn(this, "Invalid username or password. Attempt " + attempt + " of 3");
            txtPassword.setText("");
        }
    }

    private void blockAndExit(User user){
        String message = "Maximum tries reached.";
        if(userManager.blockUser(user)){
            message = message + " The user is now blocked. Only the admin can unblock it.";
        }
        Style.warn(this, message + "\nThe system will now close.");
        System.exit(0);
    }

    private void openDashboard(User user){
        attempt = 0;
        InventoryManager inventoryManager = new InventoryManager();
        new DashboardFrame(inventoryManager, userManager, user).setVisible(true);
        dispose();
    }

    private void openSignUp(){
        new SignUpFrame().setVisible(true);
        dispose();
    }
}