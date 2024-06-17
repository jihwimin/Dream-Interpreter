package com.example.dreaminterpreterai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.regex.Pattern;

public class LoginDetailsActivity extends AppCompatActivity {

    private static final Pattern ENGLISH_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_details);

        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button loginSubmitButton = findViewById(R.id.loginSubmitButton);
        Button backButton = findViewById(R.id.backButton);

        loginSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (!isValidInput(username) || !isValidInput(password)) {
                    Toast.makeText(LoginDetailsActivity.this, "Username and password must be in English characters only", Toast.LENGTH_SHORT).show();
                } else {
                    // Implement your authentication logic here
                    // For simplicity, let's assume the username is "user" and password is "password"
                    if (username.equals("user") && password.equals("password")) {
                        Intent intent = new Intent(LoginDetailsActivity.this, InitialActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginDetailsActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginDetailsActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean isValidInput(String input) {
        return ENGLISH_PATTERN.matcher(input).matches();
    }
}
