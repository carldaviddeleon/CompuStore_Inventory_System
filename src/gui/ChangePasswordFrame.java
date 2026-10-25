package gui;

import manager.UserManager;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChangePasswordFrame extends JFrame implements ActionListener {

    private UserManager userManager;
    private User currentUser;
    private JPasswordField txtOld;
    private JPasswordField txtNew;
    private JPasswordField txtConfirm;
    private JButton btnSave;
    private JButton btnCancel;

    public ChangePasswordFrame(UserManager userManager, User user){
        this.userManager = userManager;
        this.currentUser = user;
        setupWindow();
        add(makeFormPanel(), BorderLayout.CENTER);
        btnSave.addActionListener(this);
        btnCancel.addActionListener(this);
        getRootPane().setDefaultButton(btnSave);
    }

    private void setupWindow(){
        setTitle("CompuStore - Change Password");
        setSize(420, 460);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    private JPanel makeFormPanel(){
        txtOld = Style.makePasswordField("Current Password *", "Type your current password");
        txtNew = Style.makePasswordField("New Password *", "Minimum 6 characters");
        txtConfirm = Style.makePasswordField("Confirm New Password *", "Type the new password again");
        btnSave = Style.makeButton("Save Password", 'S', "Click to save the new password (Alt+S)", Style.BLUE);
        btnCancel = Style.makeButton("Cancel", 'C', "Close without changing (Alt+C)", Style.GRAY);

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.add(new JLabel("Account: " + currentUser.getUsername(), JLabel.CENTER));
        panel.add(txtOld);
        panel.add(txtNew);
        panel.add(txtConfirm);
        panel.add(btnSave);
        panel.add(btnCancel);
        panel.add(new JLabel("* = required field"));
        return panel;
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnSave) doChangePassword();
        else if(e.getSource() == btnCancel) dispose();
    }

    private void doChangePassword(){
        if(!validateInputs()) return;

        String newPassword = new String(txtNew.getPassword());
        if(userManager.changePassword(currentUser, newPassword)){
            Style.info(this, "Password changed successfully");
            dispose();
        }else{
            Style.warn(this, "The new password could not be saved");
        }
    }

    private boolean validateInputs(){
        String oldPass = new String(txtOld.getPassword());
        String newPass = new String(txtNew.getPassword());
        String confirm = new String(txtConfirm.getPassword());

        if(oldPass.isEmpty() || newPass.isEmpty() || confirm.isEmpty()){
            Style.warn(this, "Please fill in all required fields");
            return false;
        }
        if(!userManager.checkPassword(currentUser, oldPass)){
            Style.warn(this, "Current password is incorrect");
            return false;
        }
        return checkNewPassword(newPass, confirm);
    }

    private boolean checkNewPassword(String newPass, String confirm){
        if(newPass.length() < 6){
            Style.warn(this, "New password must be at least 6 characters");
            return false;
        }
        if(!newPass.equals(confirm)){
            Style.warn(this, "New passwords do not match");
            return false;
        }
        return true;
    }
}
