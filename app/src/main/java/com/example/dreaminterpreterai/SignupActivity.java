package com.example.dreaminterpreterai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private static final Pattern ENGLISH_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        EditText signupUsernameEditText = findViewById(R.id.signupUsernameEditText);
        EditText signupEmailEditText = findViewById(R.id.signupEmailEditText);
        EditText signupPasswordEditText = findViewById(R.id.signupPasswordEditText);
        Button signupSubmitButton = findViewById(R.id.signupSubmitButton);
        Button backButton = findViewById(R.id.backButton);

        signupSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = signupUsernameEditText.getText().toString();
                String email = signupEmailEditText.getText().toString();
                String password = signupPasswordEditText.getText().toString();

                if (!isValidInput(username) || !isValidInput(password)) {
                    Toast.makeText(SignupActivity.this, "Username and password must be in English characters only", Toast.LENGTH_SHORT).show();
                } else if (username.length() < 4) {
                    Toast.makeText(SignupActivity.this, "Username must be at least 4 characters", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 8) {
                    Toast.makeText(SignupActivity.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                } else {
                    // Check for unique username and save user data
                    // Assuming no overlapping usernames for simplicity
                    // Save user data (username, email, password) to your preferred storage
                    Intent intent = new Intent(SignupActivity.this, InitialActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean isValidInput(String input) {
        return ENGLISH_PATTERN.matcher(input).matches();
    }
}
