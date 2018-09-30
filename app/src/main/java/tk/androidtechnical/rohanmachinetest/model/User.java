package tk.androidtechnical.rohanmachinetest.model;

/**
 * Created by Ravi Tamada on 07/10/16.
 * www.androidhive.info
 */

public class User {

    public String email;
    public String password;

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
