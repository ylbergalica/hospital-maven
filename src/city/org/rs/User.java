package city.org.rs;

import java.util.Objects;

public class User {
    private int user_id;
    private String username;
    private String password;
    private String power_level;

    // Constructors
    public User() {
    }

    public User(int user_id, String username, String password, String power_level ) {
        this.user_id = user_id;
        this.username = username;
        this.password = password;
        this.power_level = power_level;
    }

    // Getters
    public int getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getpower_level() {
        return power_level;
    }

    // Setters
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPower_level(String power_level) {
        this.power_level = power_level ;
    }

    // Override equals and hashCode methods
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        User user = (User) obj;
        return user_id == user.user_id 
                 && Objects.equals(power_level, user.power_level)
                 && Objects.equals(username, user.username)
                 && Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_id, username, password, power_level);
    }
}
