package example.packt.com.embeddedapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.UUID;

public class AuthorizationActivity extends AppCompatActivity {

    private OAuth2Manager oauth2Manager;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        webView = (WebView) findViewById(R.id.authorization_webview);

        oauth2Manager = new OAuth2Manager(this);

        String state = generateState();
        oauth2Manager.saveState(state);

        Uri authorizationUri = new Uri.Builder()
                .scheme("http")
                .encodedAuthority(WebClient.BASE_URL)
                .path("/oauth/authorize")
                .appendQueryParameter("client_id", "clientapp")
                .appendQueryParameter("response_type", "token")
                .appendQueryParameter("redirect_uri", "oauth2://profile/callback")
                .appendQueryParameter("scope", "read_profile")
                .appendQueryParameter("state", state)
                .build();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("oauth2://profile/callback")) {
                    Intent intent = new Intent(AuthorizationActivity.this, ProfileActivity.class);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);

                    finish();
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