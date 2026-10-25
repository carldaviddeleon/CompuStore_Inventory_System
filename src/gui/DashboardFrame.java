package gui;

import manager.*;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class DashboardFrame extends JFrame implements ActionListener {

    private static int receiptCounter = FileManager.loadLastReceiptNumber() + 1;

    private InventoryManager inventoryManager;
    private UserManager userManager;
    private User currentUser;
    private ProductFrame productsFrame;
    private UsersFrame usersFrame;

    private JLabel lblTotalProducts;
    private JLabel lblTotalStock;
    private DefaultTableModel lowModel;
    private JTextField txtStockId;
    private JTextField txtStockQty;

    private JButton btnProducts;
    private JButton btnStockIn;
    private JButton btnStockOut;
    private JButton btnRefresh;
    private JButton btnLogout;
    private JMenuItem itemPassword;
    private JMenuItem itemUsers;

    public DashboardFrame(InventoryManager inventoryManager, UserManager userManager, User user){
        this.inventoryManager = inventoryManager;
        this.userManager = userManager;
        this.currentUser = user;

        setupWindow();
        setJMenuBar(makeMenuBar());
        add(makeSidebar(), BorderLayout.WEST);
        add(makeMainPanel(), BorderLayout.CENTER);
        addEvents();
        setPermissions();
        refreshDashboard();
    }

    private void setupWindow(){
        setTitle("CompuStore - Dashboard");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    private JMenuBar makeMenuBar(){
        itemPassword = new JMenuItem("Change Password");
        itemUsers = new JMenuItem("Manage Users (Admin only)");
        itemPassword.addActionListener(this);
        itemUsers.addActionListener(this);

        JMenu menuAccount = new JMenu("Account");
        menuAccount.setMnemonic('C');
        menuAccount.add(itemPassword);
        menuAccount.add(itemUsers);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menuAccount);
        return menuBar;
    }

    private JPanel makeSidebar(){
        btnProducts = Style.makeSidebarButton("PRODUCTS", 'P', "Open the Products screen (Alt+P)");
        btnStockIn = Style.makeSidebarButton("STOCK IN", 'I', "Add stock to a product (Alt+I)");
        btnStockOut = Style.makeSidebarButton("STOCK OUT", 'O', "Remove stock from a product (Alt+O)");
        btnRefresh = Style.makeSidebarButton("REFRESH", 'R', "Reload the dashboard numbers (Alt+R)");
        btnLogout = Style.makeSidebarButton("LOGOUT", 'L', "Logout from the system (Alt+L)");

        JPanel buttons = new JPanel(new GridLayout(4, 1, 0, 5));
        buttons.setBackground(Style.DARK);
        buttons.add(btnProducts);
        buttons.add(btnStockIn);
        buttons.add(btnStockOut);
        buttons.add(btnRefresh);
        return wrapSidebar(buttons);
    }

    private JPanel makeLogoPanel(){
        JLabel lblName = new JLabel("CompuStore", JLabel.CENTER);
        lblName.setFont(new Font("Arial", Font.BOLD, 18));
        lblName.setForeground(Color.WHITE);

        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(Style.DARK);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 25, 0));
        logoPanel.add(Style.makeLogo(), BorderLayout.NORTH);
        logoPanel.add(lblName, BorderLayout.CENTER);
        return logoPanel;
    }

    private JPanel wrapSidebar(JPanel buttons){
        JPanel middle = new JPanel(new BorderLayout());
        middle.setBackground(Style.DARK);
        middle.add(buttons, BorderLayout.NORTH);

        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(Style.DARK);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.add(makeLogoPanel(), BorderLayout.NORTH);
        sidebar.add(middle, BorderLayout.CENTER);
        sidebar.add(btnLogout, BorderLayout.SOUTH);
        return sidebar;
    }

    private JPanel makeMainPanel(){
        JPanel top = new JPanel(new BorderLayout(0, 15));
        top.setOpaque(false);
        top.add(makeHeader(), BorderLayout.NORTH);
        top.add(makeCardsPanel(), BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout(0, 15));
        center.setOpaque(false);
        center.add(makeStockPanel(), BorderLayout.NORTH);
        center.add(makeLowStockPanel(), BorderLayout.CENTER);

        JPanel main = new JPanel(new BorderLayout(0, 15));
        main.setBackground(Style.BACKGROUND);
        main.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        main.add(top, BorderLayout.NORTH);
        main.add(center, BorderLayout.CENTER);
        return main;
    }

    private JLabel makeHeader(){
        String text = "Dashboard   |   Logged in as: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")";
        JLabel lblHeader = new JLabel(text);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 20));
        return lblHeader;
    }

    private JPanel makeCardsPanel(){
        lblTotalProducts = new JLabel("0");
        lblTotalStock = new JLabel("0");

        JPanel cards = new JPanel(new GridLayout(1, 2, 15, 0));
        cards.setOpaque(false);
        cards.add(makeCard("Total Products", lblTotalProducts));
        cards.add(makeCard("Total Stock", lblTotalStock));
        return cards;
    }

    private JPanel makeCard(String title, JLabel lblValue){
        JLabel lblTitle = new JLabel(title, JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        lblValue.setHorizontalAlignment(JLabel.CENTER);
        lblValue.setFont(new Font("Arial", Font.BOLD, 36));
        lblValue.setForeground(Style.BLUE);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setPreferredSize(new Dimension(200, 110));
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }

    private JPanel makeStockPanel(){
        txtStockId = Style.makeField("Product ID *", "Enter the Product ID, for example P001");
        txtStockQty = Style.makeField("Quantity *", "Enter the quantity (whole number, greater than 0)");
        txtStockId.setPreferredSize(new Dimension(170, 50));
        txtStockQty.setPreferredSize(new Dimension(130, 50));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Quick Stock In / Out"));
        panel.add(txtStockId);
        panel.add(txtStockQty);
        panel.add(new JLabel("Fill in both fields, then click STOCK IN or STOCK OUT."));
        return panel;
    }

    private JPanel makeLowStockPanel(){
        lowModel = new DefaultTableModel(new String[]{"ID", "Name", "Qty", "Status"}, 0);
        JTable lowTable = new JTable(lowModel);
        Style.styleTable(lowTable);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Low Stock Report (Qty <= " + Product.LOW_STOCK_LEVEL + ")"));
        panel.add(new JScrollPane(lowTable), BorderLayout.CENTER);
        return panel;
    }

    private void addEvents(){
        btnProducts.addActionListener(this);
        btnStockIn.addActionListener(this);
        btnStockOut.addActionListener(this);
        btnRefresh.addActionListener(this);
        btnLogout.addActionListener(this);
    }

    private void setPermissions(){
        if(!currentUser.canEdit()){
            btnStockIn.setEnabled(false);
            btnStockOut.setEnabled(false);
            txtStockId.setEnabled(false);
            txtStockQty.setEnabled(false);
            itemUsers.setEnabled(false);
        }
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnProducts) openProducts();
        else if(e.getSource() == btnStockIn) doStockIn();
        else if(e.getSource() == btnStockOut) doStockOut();
        else if(e.getSource() == btnRefresh) refreshDashboard();
        else if(e.getSource() == btnLogout) doLogout();
        else if(e.getSource() == itemPassword) openChangePassword();
        else if(e.getSource() == itemUsers) openUsers();
    }

    public void refreshDashboard(){
        loadDashboardData();
        loadLowStockTable();
    }

    private void loadDashboardData(){
        lblTotalProducts.setText(String.valueOf(inventoryManager.getTotalProducts()));
        lblTotalStock.setText(String.valueOf(inventoryManager.getTotalStock()));
    }

    private void loadLowStockTable(){
        lowModel.setRowCount(0);
        ArrayList<Product> lowList = inventoryManager.getLowStockProducts();
        for(Product p : lowList){
            lowModel.addRow(new Object[]{p.getId(), p.getName(), p.getQty(), p.getStatus()});
        }
    }

    private void doStockIn(){
        if(!validateStockInputs()) return;
        int qty = readQuantity();
        if(qty == -1) return;

        Product p = findStockProduct();
        if(p == null) return;
        if(p.getQty() + qty > Product.MAX_QTY){
            Style.warn(this, "Stock cannot be more than " + Product.MAX_QTY);
            return;
        }
        int previousStock = p.getQty();
        if(!inventoryManager.stockIn(p.getId(), qty)){
            Style.warn(this, "Stock in failed. Could not save to products.txt");
            return;
        }
        showReceipt("STOCK IN", p, previousStock, qty);
        finishStock();
    }

    private void doStockOut(){
        if(!validateStockInputs()) return;
        int qty = readQuantity();
        if(qty == -1) return;

        Product p = findStockProduct();
        if(p == null) return;
        if(qty > p.getQty()){
            Style.warn(this, "Insufficient stock. Available stock is only " + p.getQty());
            return;
        }
        int previousStock = p.getQty();
        if(!inventoryManager.stockOut(p.getId(), qty)){
            Style.warn(this, "Stock out failed. Could not save to products.txt");
            return;
        }
        showReceipt("STOCK OUT", p, previousStock, qty);
        finishStock();
    }

    private Product findStockProduct(){
        Product p = inventoryManager.findBySku(txtStockId.getText().trim());
        if(p == null){
            Style.warn(this, "Product ID not found");
        }
        return p;
    }


    private void showReceipt(String type, Product p, int previousStock, int qty){
        Receipt receipt = makeReceipt(type, p, previousStock, qty);
        new ReceiptDialog(this, receipt).setVisible(true);
    }

    private Receipt makeReceipt(String type, Product p, int previousStock, int qty){
        String receiptNo = "R-" + String.format("%04d", receiptCounter++);
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));
        String processedBy = currentUser.getUsername() + " (" + currentUser.getRole() + ")";

        int remainingStock = previousStock + qty;
        if(type.equals("STOCK OUT")) remainingStock = previousStock - qty;

        return new Receipt(receiptNo, date, time, type, p.getId(), p.getName(),
                p.getPrice(), previousStock, qty, remainingStock, processedBy);
    }

    private boolean validateStockInputs(){
        if(txtStockId.getText().trim().isEmpty()){
            Style.warn(this, "Please enter Product ID first before stocking in/out.");
            return false;
        }
        if(txtStockQty.getText().trim().isEmpty()){
            Style.warn(this, "Please enter Quantity");
            return false;
        }
        return true;
    }

    private int readQuantity(){
        try{
            int qty = Integer.parseInt(txtStockQty.getText().trim());
            if(qty <= 0){
                Style.warn(this, "Quantity must be greater than 0 (no zero or negative numbers)");
                return -1;
            }
            return qty;
        }catch(NumberFormatException e){
            Style.warn(this, "Quantity must be a whole number. Letters are not allowed");
            return -1;
        }
    }

    private void finishStock(){
        txtStockId.setText("");
        txtStockQty.setText("");
        refreshDashboard();
    }

    private void openProducts(){
        if(productsFrame != null && productsFrame.isDisplayable()){
            productsFrame.toFront();
            return;
        }
        productsFrame = new ProductFrame(inventoryManager, userManager, currentUser, this);
        productsFrame.setVisible(true);
    }

    private void openChangePassword(){
        new ChangePasswordFrame(userManager, currentUser).setVisible(true);
    }

    private void openUsers(){
        if(!currentUser.canEdit()){
            Style.warn(this, "Only the admin can manage users");
            return;
        }
        if(usersFrame != null && usersFrame.isDisplayable()){
            usersFrame.toFront();
            return;
        }
        usersFrame = new UsersFrame(userManager, currentUser);
        usersFrame.setVisible(true);
    }

    private void doLogout(){
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if(choice != JOptionPane.YES_OPTION) return;

        if(productsFrame != null) productsFrame.dispose();
        if(usersFrame != null) usersFrame.dispose();
        new LoginFrame().setVisible(true);
        dispose();
    }
}
