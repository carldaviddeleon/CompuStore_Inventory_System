package manager;

import model.Product;
import java.util.ArrayList;

public class InventoryManager {

    private ArrayList<Product> productList;

    public InventoryManager(){
        productList = FileManager.loadProducts();
    }

    public boolean saveToFile(){
        return FileManager.saveProducts(productList);
    }

    public boolean addProduct(Product p){
        try{
            if(p == null) return false;
            if(isDuplicateId(p.getId())) return false;
            productList.add(p);
            return saveToFile();
        }catch(Exception e){
            return false;
        }
    }
    
    public boolean isDuplicateName(String name, String excludeId) {
        for (Product p : productList) {
            if (excludeId != null && p.getId().equalsIgnoreCase(excludeId)) {
                continue;
            }
            if (p.getName().equalsIgnoreCase(name.trim())) {
                return true;
            }
        }
        return false;
    }

    public boolean isDuplicateId(String id){
        return findBySku(id) != null;
    }

    public Product findBySku(String id){
        try{
            if(id == null || id.trim().isEmpty()) return null;
            for(Product p : productList){
                if(p.getId().equalsIgnoreCase(id.trim())) return p;
            }
        }catch(Exception e){}
        return null;
    }

    public boolean updateProduct(Product updatedP){
        try{
            if(updatedP == null) return false;
            Product oldP = findBySku(updatedP.getId());
            if(oldP == null) return false;
            int index = productList.indexOf(oldP);
            productList.set(index, updatedP);
            return saveToFile();
        }catch(Exception e){
            return false;
        }
    }

    public boolean deleteProduct(String id){
        try{
            Product p = findBySku(id);
            if(p == null) return false;
            productList.remove(p);
            return saveToFile();
        }catch(Exception e){
            return false;
        }
    }

    public boolean stockIn(String id, int addQty){
        try{
            Product p = findBySku(id);
            if(p == null || addQty <= 0) return false;
            if(p.getQty() + addQty > Product.MAX_QTY) return false;
            p.setQty(p.getQty() + addQty);
            return saveToFile();
        }catch(Exception e){
            return false;
        }
    }

    public boolean stockOut(String id, int outQty){
        try{
            Product p = findBySku(id);
            if(p == null || outQty <= 0) return false;
            if(outQty > p.getQty()) return false;
            p.setQty(p.getQty() - outQty);
            return saveToFile();
        }catch(Exception e){
            return false;
        }
    }

    public ArrayList<Product> getAllProducts(){
        return new ArrayList<>(productList);
    }

    public ArrayList<Product> getLowStockProducts(){
        ArrayList<Product> lowList = new ArrayList<>();
        for(Product p : productList){
            if(p.getQty() <= Product.LOW_STOCK_LEVEL){
                lowList.add(p);
            }
        }
        return lowList;
    }

    public int getTotalProducts(){
        return productList.size();
    }

    public int getTotalStock(){
        int total = 0;
        for(Product p : productList){
            total = total + p.getQty();
        }
        return total;
    }

    public ArrayList<Product> searchProduct(String keyword){
        ArrayList<Product> result = new ArrayList<>();
        if(keyword == null || keyword.trim().isEmpty()) return result;

        String kw = keyword.trim().toLowerCase();
        for(Product p : productList){
            if(p.getId().toLowerCase().contains(kw) || p.getName().toLowerCase().contains(kw)
                    || p.getCategory().toLowerCase().contains(kw) || p.getBrand().toLowerCase().contains(kw)){
                result.add(p);
            }
        }
        return result;
    }

    public String generateNextId(){
        int number = productList.size() + 1;
        String id = String.format("4%03d", number);
        while(findBySku(id) != null){
            number++;
            id = String.format("4%03d", number);
        }
        return id;
    }

    public boolean isCategoryInUse(String category) {
        for (Product p : productList) {
            if (p.getCategory().equalsIgnoreCase(category)) {
                return true;
            }
        }
        return false;
    }

    public void updateProductCategory(String oldCat, String newCat) {
        for (Product p : productList) {
            if (p.getCategory().equalsIgnoreCase(oldCat)) {
                p.setCategory(newCat);
            }
        }
        saveToFile();
    }
}