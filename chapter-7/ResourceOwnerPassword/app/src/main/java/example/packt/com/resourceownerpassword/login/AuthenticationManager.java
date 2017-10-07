package example.packt.com.resourceownerpassword.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AuthenticationManager {

    private final Context context;
    private final SharedPreferences sharedPreferences;

    public AuthenticationManager(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(context.getApplicationContext());
    }

    public void authenticate(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("active", true);
        editor.putString("username", user.getUsername());
        editor.putString("password", user.getPassword());
        editor.putStringSet("entries", user.getEntries());
        editor.commit();
    }

    public User getLoggedUser() {
        String username = sharedPreferences.getString("username", null);
        String password = sharedPreferences.getString("password", null);

        return new User(username, password);
    }

    public boolean isAuthenticated() {
        return sharedPreferences.getBoolean("active", false);
    }

}
