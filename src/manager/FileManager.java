package manager;

import model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.*;


public class FileManager {

    public static boolean saveProducts(ArrayList<Product> list){
        try{
            PrintWriter writer = new PrintWriter(new FileWriter("products.txt"));
            for(Product p : list){
                writer.println(p.getId() + "," + p.getName() + "," + p.getCategory() + ","
                        + p.getBrand() + "," + p.getPrice() + "," + p.getQty());
            }
            writer.close();
            return true;
        }catch(IOException e){
            System.out.println("Could not save products.txt");
            return false;
        }
    }

    public static ArrayList<Product> loadProducts(){
        ArrayList<Product> list = new ArrayList<>();
        try{
            BufferedReader reader = new BufferedReader(new FileReader("products.txt"));
            String line;
            while((line = reader.readLine()) != null){
                Product p = makeProduct(line);
                if(p != null) list.add(p);
            }
            reader.close();
        }catch(FileNotFoundException e){
            System.out.println("products.txt not found. Starting with an empty list.");
        }catch(IOException e){
            System.out.println("Could not read products.txt");
        }
        return list;
    }

    private static Product makeProduct(String line){
        try{
            String[] data = line.split(",");
            if(data.length != 6) return null;
            double price = Double.parseDouble(data[4].trim());
            int qty = Integer.parseInt(data[5].trim());
            return new Product(data[0].trim(), data[1].trim(), data[2].trim(), data[3].trim(), price, qty);
        }catch(NumberFormatException e){
            System.out.println("Skipped a bad line: " + line);
            return null;
        }
    }
    
    public static void saveDeletedToFile(Product p, String deletedBy){
        try{
            FileWriter fw = new FileWriter("deleted_products.txt", true);
            fw.write("Deleted on: " + java.time.LocalDateTime.now() + "\n");
            fw.write("Deleted by: " + deletedBy + "\n");
            fw.write("ID: " + p.getId() + ", Name: " + p.getName() + ", Category: " + p.getCategory() + ", Brand: " + p.getBrand() + ", Price: " + p.getPrice() + ", Qty: " + p.getQty() + "\n");
            fw.write("----------------------------------------\n");
            fw.close();
        }catch(Exception e){
            System.out.println("Hindi ma-save yung deleted: " + e.getMessage());
        }
    }

    public static boolean saveUsers(ArrayList<User> list){
        try{
            PrintWriter writer = new PrintWriter(new FileWriter("users.txt"));
            for(User u : list){
                writer.println(u.getUsername() + "," + u.getPassword() + "," + u.getRole() + "," + u.isBlocked());
            }
            writer.close();
            return true;
        }catch(IOException e){
            System.out.println("Could not save users.txt");
            return false;
        }
    }

    public static ArrayList<User> loadUsers(){
        ArrayList<User> list = new ArrayList<>();
        try{
            BufferedReader reader = new BufferedReader(new FileReader("users.txt"));
            String line;
            while((line = reader.readLine()) != null){
                User u = makeUser(line);
                if(u != null) list.add(u);
            }
            reader.close();
        }catch(FileNotFoundException e){
            System.out.println("users.txt not found. A new one will be created.");
        }catch(IOException e){
            System.out.println("Could not read users.txt");
        }
        return list;
    }

    private static User makeUser(String line){
        String[] data = line.split(",");
        if(data.length < 3) return null;

        User u;
        if(data[2].trim().equals("ADMIN")){
            u = new Admin(data[0].trim(), data[1].trim());
        }else{
            u = new Staff(data[0].trim(), data[1].trim());
        }
        if(data.length >= 4){
            u.setBlocked(Boolean.parseBoolean(data[3].trim()));
        }
        return u;
    }

    public static boolean saveReceipt(Receipt r){
        try{
            PrintWriter writer = new PrintWriter(new FileWriter("receipts.txt", true));
            writer.println(r.getReceiptNo() + "|" + r.getDate() + "|" + r.getTime() + "|"
                    + r.getTransactionType() + "|" + r.getProductRefNo() + "|"
                    + r.getProductName().replace("|", "/") + "|"
                    + String.format("%.2f", r.getUnitPrice()) + "|"
                    + r.getPreviousStock() + "|" + r.getQuantity() + "|" + r.getRemainingStock() + "|"
                    + String.format("%.2f", r.getTotalAmount()) + "|"
                    + r.getProcessedBy());
            writer.close();
            return true;
        }catch(IOException e){
            System.out.println("Could not save receipts.txt");
            return false;
        }
    }

    public static int loadLastReceiptNumber(){
        int last = 0;
        try{
            BufferedReader reader = new BufferedReader(new FileReader("receipts.txt"));
            String line;
            while((line = reader.readLine()) != null){
                int number = readReceiptNumber(line);
                if(number > last) last = number;
            }
            reader.close();
        }catch(FileNotFoundException e){
            System.out.println("receipts.txt not found. Receipt numbers start at R-0001.");
        }catch(IOException e){
            System.out.println("Could not read receipts.txt");
        }
        return last;
    }

    private static int readReceiptNumber(String line){
        try{
            String first = line.split("\\|")[0].trim();
            if(!first.startsWith("R-")) return 0;
            return Integer.parseInt(first.substring(2));
        }catch(NumberFormatException e){
            return 0;
        }
    }
}
