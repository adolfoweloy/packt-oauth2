package example.packt.com.resourceownerpassword.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import example.packt.com.resourceownerpassword.login.AuthenticationManager;
import example.packt.com.resourceownerpassword.R;
import example.packt.com.resourceownerpassword.login.LoginService;
import example.packt.com.resourceownerpassword.login.User;

public class MainActivity extends AppCompatActivity {

    private LoginService loginService;

    private AuthenticationManager authenticationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginService = new LoginService();

        authenticationManager = new AuthenticationManager(this);

        final TextView usernameText = findViewById(R.id.main_username);
        final TextView passwordText = findViewById(R.id.main_password);

        Button loginButton = findViewById(R.id.main_login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameText.getText().toString();
                String password = passwordText.getText().toString();
                loginService.loadUser(username, password, new LoginService.Callback() {
                    @Override
                    public void onSuccess(User user) {
                        authenticationManager.authenticate(user);

                        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailed(String message) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}
