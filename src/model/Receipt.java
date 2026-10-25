package model;

public class Receipt {

    private String receiptNo;
    private String date;
    private String time;
    private String transactionType;
    private String productRefNo;
    private String productName;
    private double unitPrice;
    private int previousStock;
    private int quantity;
    private int remainingStock;
    private double totalAmount;
    private String processedBy;

    public Receipt(String receiptNo, String date, String time, String transactionType,
                   String productRefNo, String productName, double unitPrice,
                   int previousStock, int quantity, int remainingStock, String processedBy){
        this.receiptNo = receiptNo;
        this.date = date;
        this.time = time;
        this.transactionType = transactionType;
        this.productRefNo = productRefNo;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.previousStock = previousStock;
        this.quantity = quantity;
        this.remainingStock = remainingStock;
        this.totalAmount = unitPrice * quantity;
        this.processedBy = processedBy;
    }

    public String getReceiptNo(){
        return receiptNo;
    }

    public String getDate(){
        return date;
    }

    public String getTime(){
        return time;
    }

    public void setDate(String date){
        this.date = date;
    }

    public void setTime(String time){
        this.time = time;
    }

    public String getTransactionType(){
        return transactionType;
    }

    public String getProductRefNo(){
        return productRefNo;
    }

    public String getProductName(){
        return productName;
    }

    public double getUnitPrice(){
        return unitPrice;
    }

    public int getPreviousStock(){
        return previousStock;
    }

    public int getQuantity(){
        return quantity;
    }

    public int getRemainingStock(){
        return remainingStock;
    }

    public double getTotalAmount(){
        return totalAmount;
    }

    public String getProcessedBy(){
        return processedBy;
    }

    public boolean isStockIn(){
        return transactionType.equals("STOCK IN");
    }

    @Override
    public String toString(){
        return receiptNo + " - " + transactionType + " - " + productRefNo;
    }
}
