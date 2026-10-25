package manager;


public class InputValidator {

    public static final String PASSWORD_RULE =
            "Password must be 6-30 characters, with at least one letter and one number, and no spaces.";

    private InputValidator(){ }

    public static boolean isBlank(String text){
        return text == null || text.trim().isEmpty();
    }

    public static boolean isWholeNumber(String text){
        return text != null && text.trim().matches("\\d+");
    }

    public static boolean isNegativeNumber(String text){
        return text != null && text.trim().matches("-\\s*\\d+(\\.\\d+)?");
    }

    public static boolean isValidMoney(String text){
        return text != null && text.trim().matches("\\d+(\\.\\d{1,2})?");
    }

    public static boolean hasPipe(String text){
        return text != null && text.contains("|");
    }

    public static boolean isValidUsername(String text){
        return text != null && text.matches("[A-Za-z0-9_]{3,15}");
    }

    public static boolean isValidPassword(String text){
        if(text == null) return false;
        if(text.length() < 6 || text.length() > 30) return false;
        if(text.contains(" ")) return false;

        boolean hasLetter = false;
        boolean hasDigit = false;
        for(char c : text.toCharArray()){
            if(Character.isLetter(c)) hasLetter = true;
            if(Character.isDigit(c)) hasDigit = true;
        }
        return hasLetter && hasDigit;
    }
}
