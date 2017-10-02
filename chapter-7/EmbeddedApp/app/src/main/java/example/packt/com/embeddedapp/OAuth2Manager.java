package example.packt.com.embeddedapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class OAuth2Manager {

    private final SharedPreferences prefs;

    public OAuth2Manager(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void saveState(String state) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("state", state);
        editor.commit();
    }

    public String getState() {
        return prefs.getString("state", null);
    }

}
