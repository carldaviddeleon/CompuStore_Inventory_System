package gui;

import manager.FileManager;
import model.Receipt;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;


public class ReceiptDialog extends JDialog implements ActionListener {


    private static final Color YELLOW = new Color(255, 255, 204);
    private static final Color GREEN = new Color(22, 163, 74);
    private static final Color RED = new Color(220, 38, 38);
    private static final Font NORMAL = new Font("Arial", Font.PLAIN, 12);
    private static final Font BOLD = new Font("Arial", Font.BOLD, 12);

    private Receipt receipt;
    private DecimalFormat money = new DecimalFormat("#,##0.00");
    private JTextField txtDate;
    private JTextField txtTime;
    private JButton btnSave;
    private JButton btnClose;

    public ReceiptDialog(JFrame parent, Receipt receipt){
        super(parent, "Receipt " + receipt.getReceiptNo(), true);
        this.receipt = receipt;
        setupWindow(parent);
        add(makeHeaderPanel(), BorderLayout.NORTH);
        add(makeDetailsPanel(), BorderLayout.CENTER);
        add(makeBottomPanel(), BorderLayout.SOUTH);
        btnSave.addActionListener(this);
        btnClose.addActionListener(this);
    }

    private void setupWindow(JFrame parent){
        setSize(350, 500);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());
    }

    private JPanel makeHeaderPanel(){
        JLabel lblStore = new JLabel("COMPUTER ACCESSORY STORE", JLabel.CENTER);
        lblStore.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel lblTitle = new JLabel("INVENTORY TRANSACTION RECEIPT", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 11));

        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 8, 20));
        panel.add(lblStore);
        panel.add(lblTitle);
        return panel;
    }

    private JPanel makeDetailsPanel(){
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        addTransactionRows(panel);
        addProductRows(panel);
        addStockRows(panel);
        return panel;
    }

    private void addTransactionRows(JPanel panel){
        txtDate = makeTypableBox(receipt.getDate(), "Type the date like this: MM/dd/yyyy  (example 10/19/2026)");
        txtTime = makeTypableBox(receipt.getTime(), "Type the time like this: hh:mm AM or hh:mm PM  (example 10:30 AM)");

        panel.add(makeRow("Receipt No.:", makeBoldLabel(receipt.getReceiptNo())));
        panel.add(makeRow("Date:", txtDate));
        panel.add(makeRow("Time:", txtTime));
        panel.add(makeRow("Transaction:", makeTransactionLabel()));
    }

    private void addProductRows(JPanel panel){
        panel.add(makeLine());
        panel.add(makeRow("Product Details", new JLabel("")));
        panel.add(makeRow("Product Ref No.:", makeBoldLabel(receipt.getProductRefNo())));
        panel.add(makeNameRow());
        panel.add(makeRow("Unit Price:", makeBoldLabel(money.format(receipt.getUnitPrice()))));
    }

    private void addStockRows(JPanel panel){
        String quantityLabel = "Quantity Taken:";
        if(receipt.isStockIn()) quantityLabel = "Quantity Added:";

        panel.add(makeLine());
        panel.add(makeRow("Previous Stock:", makeBoldLabel(receipt.getPreviousStock() + " pcs")));
        panel.add(makeRow(quantityLabel, makeBoldLabel(receipt.getQuantity() + " pcs")));
        panel.add(makeRow("Remaining Stock:", makeBoldLabel(receipt.getRemainingStock() + " pcs")));
    }

    private JPanel makeRow(String text, JComponent value){
        JLabel lblLeft = new JLabel(text);
        lblLeft.setFont(NORMAL);
        if(text.equals("Product Details")) lblLeft.setFont(BOLD);

        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.add(lblLeft, BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);
        return row;
    }

    private JPanel makeNameRow(){
        String name = receipt.getProductName();
        JLabel lblName = new JLabel(name, JLabel.RIGHT);
        lblName.setFont(BOLD);
        if(name.length() > 34){
            lblName.setFont(new Font("Arial", Font.PLAIN, 9));
        }else if(name.length() > 28){
            lblName.setFont(new Font("Arial", Font.BOLD, 10));
        }

        JLabel lblLeft = new JLabel("Product Name:");
        lblLeft.setFont(NORMAL);

        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.add(lblLeft, BorderLayout.WEST);
        row.add(lblName, BorderLayout.CENTER);
        return row;
    }

    private JLabel makeBoldLabel(String text){
        JLabel label = new JLabel(text);
        label.setFont(BOLD);
        return label;
    }

    private JTextField makeTypableBox(String text, String tip){
        JTextField box = new JTextField(text, 15);
        box.setFont(NORMAL);
        box.setBackground(YELLOW);
        box.setToolTipText(tip);
        return box;
    }

    private JLabel makeTransactionLabel(){
        JLabel label = new JLabel(receipt.getTransactionType(), JLabel.CENTER);
        label.setOpaque(true);
        label.setFont(BOLD);
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        label.setBackground(RED);
        if(receipt.isStockIn()) label.setBackground(GREEN);
        return label;
    }

    private JPanel makeLine(){
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setBackground(Color.WHITE);
        panel.add(new JLabel(""));
        panel.add(new JSeparator());
        return panel;
    }

    private JPanel makeBottomPanel(){
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(Color.WHITE);
        totalPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 5, 20));
        totalPanel.add(new JSeparator(), BorderLayout.NORTH);
        totalPanel.add(makeTotalRow(), BorderLayout.CENTER);
        totalPanel.add(makeProcessedByLabel(), BorderLayout.SOUTH);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(totalPanel, BorderLayout.NORTH);
        panel.add(makeButtonPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel makeTotalRow(){
        JLabel lblTotal = new JLabel("TOTAL AMOUNT");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));

        String amountText = money.format(receipt.getTotalAmount());
        JLabel lblAmount = new JLabel(amountText, JLabel.RIGHT);
        lblAmount.setForeground(GREEN);
        lblAmount.setFont(new Font("Arial", Font.BOLD, 20));
        if(amountText.length() > 12) lblAmount.setFont(new Font("Arial", Font.BOLD, 15));

        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createEmptyBorder(6, 0, 4, 0));
        row.add(lblTotal, BorderLayout.WEST);
        row.add(lblAmount, BorderLayout.CENTER);
        return row;
    }

    private JLabel makeProcessedByLabel(){
        JLabel label = new JLabel("Processed by: " + receipt.getProcessedBy(), JLabel.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 11));
        return label;
    }

    private JPanel makeButtonPanel(){
        btnSave = new JButton("Save Receipt");
        btnSave.setBackground(new Color(37, 99, 235));
        btnSave.setForeground(Color.WHITE);
        btnSave.setMnemonic('S');
        btnSave.setToolTipText("Save this receipt to receipts.txt (Alt+S)");

        btnClose = new JButton("Close");
        btnClose.setBackground(new Color(100, 116, 139));
        btnClose.setForeground(Color.WHITE);
        btnClose.setMnemonic('C');
        btnClose.setToolTipText("Close without saving (Alt+C)");

        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 20, 12, 20));
        panel.add(btnSave);
        panel.add(btnClose);
        return panel;
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnSave) saveReceipt();
        else if(e.getSource() == btnClose) closeReceipt();
    }

    private void saveReceipt(){
        if(!validateInputs()) return;

        receipt.setDate(txtDate.getText().trim());
        receipt.setTime(txtTime.getText().trim());

        if(FileManager.saveReceipt(receipt)){
            JOptionPane.showMessageDialog(this, "Receipt " + receipt.getReceiptNo() + " was saved to receipts.txt",
                    "Saved", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }else{
            showError("The receipt could not be saved to receipts.txt");
        }
    }

    private void closeReceipt(){
        int choice = JOptionPane.showConfirmDialog(this, "The receipt is not saved yet. Close without saving?",
                "Close", JOptionPane.YES_NO_OPTION);
        if(choice == JOptionPane.YES_OPTION){
            dispose();
        }
    }

    private boolean validateInputs(){
        if(!checkEmpty()) return false;
        if(!checkFormat()) return false;
        return true;
    }

    private boolean checkEmpty(){
        if(txtDate.getText().trim().isEmpty()){
            showError("Please enter the Date");
            return false;
        }
        if(txtTime.getText().trim().isEmpty()){
            showError("Please enter the Time");
            return false;
        }
        return true;
    }

    private boolean checkFormat(){
        if(!isValidDate(txtDate.getText().trim())){
            showError("Date must look like MM/dd/yyyy, example 10/19/2026");
            return false;
        }
        if(!isValidTime(txtTime.getText().trim())){
            showError("Time must look like hh:mm AM or hh:mm PM, example 10:30 AM");
            return false;
        }
        return true;
    }

    private boolean isValidDate(String date){
        if(date.length() != 10) return false;
        if(date.charAt(2) != '/' || date.charAt(5) != '/') return false;
        try{
            int month = Integer.parseInt(date.substring(0, 2));
            int day = Integer.parseInt(date.substring(3, 5));
            int year = Integer.parseInt(date.substring(6));
            return month >= 1 && month <= 12 && day >= 1 && day <= 31 && year >= 2000;
        }catch(NumberFormatException e){
            return false;
        }
    }

    private boolean isValidTime(String time){
        if(time.length() != 8) return false;
        if(time.charAt(2) != ':' || time.charAt(5) != ' ') return false;
        String ampm = time.substring(6).toUpperCase();
        if(!ampm.equals("AM") && !ampm.equals("PM")) return false;
        try{
            int hour = Integer.parseInt(time.substring(0, 2));
            int minute = Integer.parseInt(time.substring(3, 5));
            return hour >= 1 && hour <= 12 && minute >= 0 && minute <= 59;
        }catch(NumberFormatException e){
            return false;
        }
    }

    private void showError(String message){
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
