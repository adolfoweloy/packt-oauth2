package example.packt.com.embeddedapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.UUID;

public class AuthorizationActivity extends AppCompatActivity {

    private OAuth2Manager oauth2Manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        oauth2Manager = new OAuth2Manager(getApplicationContext());

        String state = generateState();
        oauth2Manager.saveState(state);

        Uri authorizationUri = new Uri.Builder()
                .scheme("http")
                .encodedAuthority("192.168.0.167:8080")
                .path("/oauth/authorize")
                .appendQueryParameter("client_id", "clientapp")
                .appendQueryParameter("response_type", "token")
                .appendQueryParameter("redirect_uri", "oauth2://profile/callback")
//                .appendQueryParameter("redirect_uri", "http://localhost:9000/callback")
                .appendQueryParameter("scope", "read_profile")
                .appendQueryParameter("state", state)
                .build();

        WebView webView = (WebView) findViewById(R.id.authorization_webview);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.contains("oauth2://profile/callback")) {
                    Intent intent = new Intent(AuthorizationActivity.this, ProfileActivity.class);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }

                return false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });
        webView.loadUrl(authorizationUri.toString());

    }

    private String generateState() {
        return UUID.randomUUID().toString();
    }
}
