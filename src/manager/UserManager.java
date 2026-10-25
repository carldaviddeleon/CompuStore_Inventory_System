package manager;

import model.Admin;
import model.User;
import java.util.ArrayList;

public class UserManager {

    private ArrayList<User> userList;

    public UserManager(){
        userList = FileManager.loadUsers();
        if(userList.isEmpty()){
            createDefaultAdmin();
        }
    }

    private void createDefaultAdmin(){
        userList.add(new Admin("admin", makeHash("admin123")));
        saveToFile();
    }

    public boolean saveToFile(){
        return FileManager.saveUsers(userList);
    }

    public User findUser(String username){
        if(username == null || username.trim().isEmpty()) return null;
        for(User u : userList){
            if(u.getUsername().equalsIgnoreCase(username.trim())) return u;
        }
        return null;
    }

    public boolean isDuplicateUsername(String username){
        return findUser(username) != null;
    }

    public boolean addUser(User u){
        if(u == null || isDuplicateUsername(u.getUsername())) return false;
        userList.add(u);
        return saveToFile();
    }

    public String makeHash(String plainPassword){
        return String.valueOf(plainPassword.hashCode());
    }

    public boolean checkPassword(User u, String plainPassword){
        if(u == null || plainPassword == null) return false;
        return u.getPassword().equals(makeHash(plainPassword));
    }

    public boolean changePassword(User u, String newPlainPassword){
        if(u == null) return false;
        u.setPassword(makeHash(newPlainPassword));
        return saveToFile();
    }


    public boolean blockUser(User u){
        if(u == null) return false;
        if(u.getUsername().equalsIgnoreCase("admin")) return false;
        u.setBlocked(true);
        saveToFile();
        return true;
    }

    public void unblockUser(User u){
        if(u == null) return;
        u.setBlocked(false);
        saveToFile();
    }

    public ArrayList<User> getAllUsers(){
        return new ArrayList<>(userList);
    }
}
