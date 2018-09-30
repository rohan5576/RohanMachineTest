package tk.androidtechnical.rohanmachinetest.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Constant {

    public static boolean emailCheck(String email) {
        try {
            Pattern pattern = Pattern.compile("^([a-z0-9\\+_\\-]+)(\\.[a-z0-9\\+_\\-]+)*@([a-z0-9\\-]+\\.)+[a-z]{2,6}$");
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        } catch(Exception ex) {
            return false;
        }
    }


}
