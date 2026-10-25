package model;

public class Staff extends User {

    public Staff(String username, String password){
        super(username, password);
    }

    @Override
    public String getRole(){
        return "STAFF";
    }

    @Override
    public boolean canEdit(){
        return false;
    }
}
