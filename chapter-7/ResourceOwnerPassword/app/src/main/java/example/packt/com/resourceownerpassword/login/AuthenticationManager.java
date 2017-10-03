package example.packt.com.resourceownerpassword.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import example.packt.com.resourceownerpassword.client.oauth2.AccessToken;

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

    public void setAccessToken(AccessToken accessToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("oauth_authorized", true);
        editor.putString("access_token", accessToken.getValue());
        editor.putLong("expires_in", accessToken.getExpiresIn());
        editor.putString("token_type", accessToken.getTokenType());
        editor.putString("scope", accessToken.getScope());
        editor.commit();
    }

    public AccessToken getAccessToken() {
        boolean authorized = sharedPreferences.getBoolean("oauth_authorized", false);

        if (!authorized) return null;

        AccessToken token = new AccessToken();
        token.setValue(sharedPreferences.getString("access_token", null));
        token.setExpiresIn(sharedPreferences.getLong("expires_in", 0));
        token.setScope(sharedPreferences.getString("scope", null));
        token.setTokenType(sharedPreferences.getString("token_type", null));
        return token;
    }
}
