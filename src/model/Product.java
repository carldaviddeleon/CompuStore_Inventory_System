package model;


public class Product {

    public static final int LOW_STOCK_LEVEL = 5;
    public static final int MAX_QTY = 10000;

    private String id;
    private String name;
    private String category;
    private String brand;
    private double price;
    private int qty;

    public Product(String id, String name, String category, String brand, double price, int qty){
        this.id = id;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.qty = qty;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getCategory(){
        return category;
    }

    public void setCategory(String category){
        this.category = category;
    }

    public String getBrand(){
        return brand;
    }

    public void setBrand(String brand){
        this.brand = brand;
    }

    public double getPrice(){
        return price;
    }

    public void setPrice(double price){
        this.price = price;
    }

    public int getQty(){
        return qty;
    }

    public void setQty(int qty){
        this.qty = qty;
    }

    public String getStatus(){
        if(qty == 0) return "Out of Stock";
        if(qty <= LOW_STOCK_LEVEL) return "Low Stock";
        return "In Stock";
    }

    @Override
    public String toString(){
        return id + " - " + name + " (" + brand + ")";
    }
}
