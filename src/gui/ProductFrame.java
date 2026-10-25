package gui;

import manager.InventoryManager;
import manager.UserManager;
import model.Product;
import model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ProductFrame extends JFrame implements ActionListener {

    private InventoryManager inventoryManager;
    private UserManager userManager;
    private User currentUser;
    private DashboardFrame dashboard;

    private JTextField txtId;
    private JTextField txtName;
    private JComboBox<String> cbCategory;
    private JTextField txtBrand;
    private JTextField txtPrice;
    private JTextField txtQty;
    private JTextField txtSearch;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnSearch;
    private JButton btnRefresh;
    private JButton btnBack;
    private JButton btnEditCategory;

    private JTable table;
    private DefaultTableModel tableModel;
    
    ArrayList <String> categories = new ArrayList<>();

    public ProductFrame(InventoryManager inventoryManager, UserManager userManager, User user, DashboardFrame dashboard){
        this.inventoryManager = inventoryManager;
        this.userManager = userManager;
        this.currentUser = user;
        this.dashboard = dashboard;

        setupWindow();
        add(makeTopPanel(), BorderLayout.NORTH);
        add(makeTablePanel(), BorderLayout.CENTER);
        addEvents();
        setPermissions();
        autoGenerateId();
        loadTable();
    }

    private void setupWindow(){
        setTitle("CompuStore - Products");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Style.BACKGROUND);
    }

    private JPanel makeTopPanel(){
        JLabel lblTitle = new JLabel("Product Management   (* = required field)");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.setBackground(Style.BACKGROUND);
        top.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        top.add(lblTitle, BorderLayout.NORTH);
        top.add(makeFieldsPanel(), BorderLayout.CENTER);
        top.add(makeButtonPanel(), BorderLayout.SOUTH);
        return top;
    }

    private JPanel makeFieldsPanel(){
        txtId = Style.makeField("Product ID *", "Auto-numbered ID like 4001 (you cannot type here)");
        txtId.setEditable(false);
        txtName = Style.makeField("Product Name *", "Enter the product name");
        txtBrand = Style.makeField("Brand *", "Enter the brand name");
        txtPrice = Style.makeField("Price *", "Enter the price, numbers only, greater than 0");
        txtQty = Style.makeField("Quantity *", "Enter the quantity, whole numbers only");
        makeCategoryCombo();

        JPanel panel = new JPanel(new GridLayout(2, 3, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(txtId);
        panel.add(txtName);
        panel.add(cbCategory);
        panel.add(txtBrand);
        panel.add(txtPrice);
        panel.add(txtQty);
        return panel;
    }

    private void makeCategoryCombo(){
        categories.clear();
        categories.add("Laptop");
        categories.add("Accessories");
        categories.add("Peripherals");
        categories.add("Components"); // fixed typo
        
        cbCategory = new JComboBox<>(categories.toArray(new String[0]));
        cbCategory.setBorder(BorderFactory.createTitledBorder("Category *"));
        cbCategory.setToolTipText("Select the product category");   
    }
    
    private void editCategory(){
        JDialog dialog = new JDialog(this, "Edit Categories", true);
        dialog.setSize(360, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setLayout(new GridLayout(5, 1, 5, 5));
        ((JPanel) dialog.getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        final JComboBox<String> cbList = new JComboBox<>(categories.toArray(new String[0]));
        cbList.setBorder(BorderFactory.createTitledBorder("Select Category"));

        final JTextField txtNew = new JTextField();
        txtNew.setBorder(BorderFactory.createTitledBorder("New Category Name"));

       JButton btnAdd = new JButton("Add");
       JButton btnUpdate = new JButton("Update");
       JButton btnRemove = new JButton("Remove");

       dialog.add(cbList);
       dialog.add(txtNew);
       dialog.add(btnAdd);
       dialog.add(btnUpdate);
       dialog.add(btnRemove);

       btnAdd.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               String cat = txtNew.getText().trim();
               if (cat.isEmpty()) { Style.warn(dialog, "Empty!"); return; }
               for (String c : categories) if (c.equalsIgnoreCase(cat)) { Style.warn(dialog, "Already exists."); return; }
               categories.add(cat);
               cbCategory.addItem(cat);
               cbList.addItem(cat);
               cbList.setSelectedItem(cat);
               txtNew.setText("");
               Style.info(dialog, "Added!");
            }
        });

       btnUpdate.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               String oldCat = (String) cbList.getSelectedItem();
               String newCat = txtNew.getText().trim();
               if (oldCat == null || newCat.isEmpty()) { Style.warn(dialog, "Select and type new name."); return; }
               for (String c : categories) if (c.equalsIgnoreCase(newCat) && !c.equalsIgnoreCase(oldCat)) { Style.warn(dialog, "Already exists."); return; }

               categories.set(categories.indexOf(oldCat), newCat);
               cbCategory.removeItem(oldCat);
               cbCategory.addItem(newCat);
               cbList.removeItem(oldCat);
               cbList.addItem(newCat);
               inventoryManager.updateProductCategory(oldCat, newCat);
               loadTable();
               Style.info(dialog, "Updated!");
            }
        });

        btnRemove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String cat = (String) cbList.getSelectedItem();
                if (cat == null) return;
                if (inventoryManager.isCategoryInUse(cat)) { Style.warn(dialog, "May product pa na gumagamit."); return; }
                if (JOptionPane.showConfirmDialog(dialog, "Remove " + cat + "?") == JOptionPane.YES_OPTION) {
                    categories.remove(cat);
                    cbCategory.removeItem(cat);
                    cbList.removeItem(cat);
                    Style.info(dialog, "Removed!");
                }
            }
        });

         dialog.setVisible(true);
    }

    private void makeButtons(){
        btnAdd = Style.makeButton("Add", 'A', "Click to add a new product (Alt+A)", Style.BLUE);
        btnUpdate = Style.makeButton("Update", 'U', "Click to update the selected product (Alt+U)", Style.BLUE);
        btnDelete = Style.makeButton("Delete", 'D', "Click to delete the selected product (Alt+D). Needs the admin password", Style.RED);
        btnClear = Style.makeButton("Clear", 'C', "Click to clear the fields (Alt+C)", Style.GRAY);
        txtSearch = Style.makeField("Search", "Type an ID, name, category or brand, then click Search");
        txtSearch.setPreferredSize(new Dimension(220, 50));
        btnSearch = Style.makeButton("Search", 'S', "Click to search (Alt+S)", Style.BLUE);
        btnRefresh = Style.makeButton("Refresh", 'R', "Click to show all products again (Alt+R)", Style.GRAY);
        btnBack = Style.makeButton("Back", 'B', "Click to go back to the dashboard (Alt+B)", Style.GRAY);
        btnEditCategory = Style.makeButton("Edit Categories", 'E', "Click to Edit Categories (Alt+E)", Color.BLUE);
    }

    private JPanel makeButtonPanel(){
        makeButtons();
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(Color.WHITE);
        panel.add(btnAdd);
        panel.add(btnUpdate);
        panel.add(btnDelete);
        panel.add(btnClear);
        panel.add(txtSearch);
        panel.add(btnSearch);
        panel.add(btnRefresh);
        panel.add(btnBack);
        panel.add(btnEditCategory);
        return panel;
    }

    private JPanel makeTablePanel(){
    String[] columns = {"ID", "Name", "Category", "Brand", "Price", "Qty", "Status"};
    tableModel = new DefaultTableModel(columns, 0);
    table = new JTable(tableModel);
    Style.styleTable(table);
    table.getColumnModel().getColumn(1).setPreferredWidth(300);
    table.setToolTipText("Click a row to show it in the fields above");

    table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            String status = "";
            if (value != null) status = value.toString();
            if (status.equals("In Stock")) {
                c.setBackground(Color.GREEN);
            } else if (status.equals("Low Stock")) {
                c.setBackground(Color.YELLOW);
            } else if (status.equals("Out of Stock")) {
                c.setBackground(Color.RED);
            } else {
                c.setBackground(Color.WHITE);
            }
            return c;
        }
    });

    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(Style.BACKGROUND);
    panel.setBorder(BorderFactory.createEmptyBorder(5, 20, 20, 20));
    panel.add(new JScrollPane(table), BorderLayout.CENTER);
    return panel;
}

    private void addEvents(){
        btnAdd.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnDelete.addActionListener(this);
        btnClear.addActionListener(this);
        btnSearch.addActionListener(this);
        txtSearch.addActionListener(this);
        btnRefresh.addActionListener(this);
        btnBack.addActionListener(this);
        btnEditCategory.addActionListener(this);

        table.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                fillFields();
            }
        });
        addWindowListener(new WindowAdapter(){
            public void windowClosed(WindowEvent e){
                dashboard.refreshDashboard();
            }
        });
    }

    private void setPermissions(){
        if(!currentUser.canEdit()){
            btnAdd.setEnabled(false);
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
            btnEditCategory.setEnabled(false);
        }
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource() == btnAdd) addProduct();
        else if(e.getSource() == btnUpdate) updateProduct();
        else if(e.getSource() == btnDelete) deleteProduct();
        else if(e.getSource() == btnClear) clearAll();
        else if(e.getSource() == btnSearch || e.getSource() == txtSearch) searchProduct();
        else if(e.getSource() == btnRefresh) refreshTable();
        else if(e.getSource() == btnBack) dispose();
        else if(e.getSource() == btnEditCategory) editCategory();
    }

    private void autoGenerateId(){
        txtId.setText(inventoryManager.generateNextId());
    }

    private void loadTable(){
        fillTable(inventoryManager.getAllProducts());
    }

    private void fillTable(ArrayList<Product> list){
        tableModel.setRowCount(0);
        for(Product p : list){
            tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getCategory(),
                                           p.getBrand(), p.getPrice(), p.getQty(), p.getStatus()});
        }
    }

    private void fillFields(){
        int row = table.getSelectedRow();
        if(row == -1) return;
        txtId.setText(tableModel.getValueAt(row, 0).toString());
        txtName.setText(tableModel.getValueAt(row, 1).toString());
        cbCategory.setSelectedItem(tableModel.getValueAt(row, 2).toString());
        txtBrand.setText(tableModel.getValueAt(row, 3).toString());
        txtPrice.setText(tableModel.getValueAt(row, 4).toString());
        txtQty.setText(tableModel.getValueAt(row, 5).toString());
    }

    private void clearFields(){
        txtName.setText("");
        txtBrand.setText("");
        txtPrice.setText("");
        txtQty.setText("");
        txtSearch.setText("");
        cbCategory.setSelectedIndex(0);
        table.clearSelection();
        autoGenerateId();
    }

    private void clearAll(){
        clearFields();
        loadTable();
    }

    private void refreshTable(){
        txtSearch.setText("");
        loadTable();
    }

    private boolean validateInputs(){
        if(!checkRequiredFields()) return false;
        if(!checkNoComma()) return false;
        if(!checkPrice()) return false;
        if(!checkQuantity()) return false;
        return true;
    }

    private boolean checkRequiredFields(){
        if(txtName.getText().trim().isEmpty()){
            Style.warn(this, "Please enter Product Name");
            return false;
        }
        if(txtBrand.getText().trim().isEmpty()){
            Style.warn(this, "Please enter Brand");
            return false;
        }
        if(txtPrice.getText().trim().isEmpty()){
            Style.warn(this, "Please enter Price");
            return false;
        }
        if(txtQty.getText().trim().isEmpty()){
            Style.warn(this, "Please enter Quantity");
            return false;
        }
        return true;
    }

    private boolean checkNoComma(){
        if(txtName.getText().contains(",") || txtBrand.getText().contains(",")){
            Style.warn(this, "Product Name and Brand cannot contain a comma");
            return false;
        }
        return true;
    }

    private boolean checkPrice(){
        try{
            double price = Double.parseDouble(txtPrice.getText().trim());
            if(Double.isNaN(price) || price <= 0){
                Style.warn(this, "Price must be greater than 0 (no zero or negative numbers)");
                return false;
            }
            if(price > 1000000){
                Style.warn(this, "Price is too high. The maximum is 1,000,000");
                return false;
            }
        }catch(NumberFormatException e){
            Style.warn(this, "Price must be a number. Letters are not allowed");
            return false;
        }
        return true;
    }

    private boolean checkQuantity(){
        try{
            int qty = Integer.parseInt(txtQty.getText().trim());
            if(qty < 0){
                Style.warn(this, "Quantity cannot be negative");
                return false;
            }
            if(qty > Product.MAX_QTY){
                Style.warn(this, "Quantity is too large. The maximum is " + Product.MAX_QTY);
                return false;
            }
        }catch(NumberFormatException e){
            Style.warn(this, "Quantity must be a whole number. Letters are not allowed");
            return false;
        }
        return true;
    }

    private Product makeProductFromFields(){
        return new Product(txtId.getText().trim(),
                txtName.getText().trim(),
                cbCategory.getSelectedItem().toString(),
                txtBrand.getText().trim(),
                Double.parseDouble(txtPrice.getText().trim()),
                Integer.parseInt(txtQty.getText().trim()));
    }

    private void addProduct(){     
        if(!validateInputs()) return;
        String productName = txtName.getText().trim();
            if (inventoryManager.isDuplicateName(productName, null)) {
        JOptionPane.showMessageDialog(this, "Product name already exists!", "Duplicate", JOptionPane.WARNING_MESSAGE);
        return;
    }
        if(inventoryManager.isDuplicateId(txtId.getText().trim())){
            Style.warn(this, "Product ID already exists. Click Clear to make a new ID.");
            return;
        }
        if(inventoryManager.addProduct(makeProductFromFields())){
            Style.info(this, "Product added successfully");
            loadTable();
            clearFields();
        }else{
            Style.warn(this, "The product could not be saved");
        }
    }

    private void updateProduct(){
        if(table.getSelectedRow() == -1){
            Style.warn(this, "Please select a product from the table");
            return;
        }
        if(!validateInputs()) return;
        String id = txtId.getText().trim();
        String productName = txtName.getText().trim();
            if (inventoryManager.isDuplicateName(productName, id)) {
        JOptionPane.showMessageDialog(this, "Product name already exists!", "Duplicate", JOptionPane.WARNING_MESSAGE);
        return;
    }
        if(inventoryManager.updateProduct(makeProductFromFields())){
            Style.info(this, "Product updated successfully");
            loadTable();
            clearFields();
        }else{
            Style.warn(this, "The product could not be updated");
        }
    }

    private void deleteProduct(){
        if(table.getSelectedRow() == -1){
            Style.warn(this, "Please select a product from the table");
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this product?", "Delete", JOptionPane.YES_NO_OPTION);
        if(choice != JOptionPane.YES_OPTION) return;
        if(!askAdminPassword()) return;

        if(inventoryManager.deleteProduct(txtId.getText().trim())){
            Style.info(this, "Product deleted successfully");
            loadTable();
            clearFields();
        }else{
            Style.warn(this, "The product could not be deleted");
        }
    }

    private boolean askAdminPassword(){
        JPasswordField txtAdminPass = new JPasswordField();
        int choice = JOptionPane.showConfirmDialog(this, txtAdminPass, "Enter the admin password", JOptionPane.OK_CANCEL_OPTION);
        if(choice != JOptionPane.OK_OPTION) return false;

        String typed = new String(txtAdminPass.getPassword());
        if(typed.isEmpty()){
            Style.warn(this, "Admin password is required to delete");
            return false;
        }
        if(!userManager.checkPassword(currentUser, typed)){
            Style.warn(this, "Wrong admin password");
            return false;
        }
        return true;
    }

    private void searchProduct(){
        String keyword = txtSearch.getText().trim();
        if(keyword.isEmpty()){
            Style.warn(this, "Please enter a keyword to search");
            return;
        }
        ArrayList<Product> result = inventoryManager.searchProduct(keyword);
        if(result.isEmpty()){
            Style.info(this, "No product found");
            return;
        }
        fillTable(result);
    }
}