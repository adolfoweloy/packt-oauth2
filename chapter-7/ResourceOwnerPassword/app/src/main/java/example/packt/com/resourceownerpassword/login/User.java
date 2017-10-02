package example.packt.com.resourceownerpassword.login;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Set<String> getEntries() {
        return new HashSet<>(Arrays.asList("entry a", "entry b"));
    }

}
