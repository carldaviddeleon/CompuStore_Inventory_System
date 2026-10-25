package model;

public abstract class User {

    private String username;
    private String password;
    private boolean isBlocked;

    public User(String username, String password){
        this.username = username;
        this.password = password;
        this.isBlocked = false;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public boolean isBlocked(){
        return isBlocked;
    }

    public void setBlocked(boolean blocked){
        this.isBlocked = blocked;
    }

    public String getRole(){
        return "USER";
    }

    public boolean canEdit(){
        return false;
    }
}
