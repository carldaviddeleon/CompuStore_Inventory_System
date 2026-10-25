package model;

public class Admin extends User {

    public Admin(String username, String password){
        super(username, password);
    }

    @Override
    public String getRole(){
        return "ADMIN";
    }

    @Override
    public boolean canEdit(){
        return true;
    }
}
