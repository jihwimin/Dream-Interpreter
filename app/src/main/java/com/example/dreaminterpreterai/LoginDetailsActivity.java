package com.example.dreaminterpreterai;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class LoginDetailsActivity extends AppCompatActivity {

    private static final Pattern ENGLISH_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");
    private AppDatabase db;
    private UserDao userDao;
    private ExecutorService executorService;
    private static final String TAG = "LoginDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_details);

        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button loginSubmitButton = findViewById(R.id.loginSubmitButton);
        Button backButton = findViewById(R.id.backButton);

        db = AppDatabase.getInstance(this);
        userDao = db.userDao();
        executorService = Executors.newSingleThreadExecutor();

        loginSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (!isValidInput(username) || !isValidInput(password)) {
                    Toast.makeText(LoginDetailsActivity.this, "Username and password must be in English characters only", Toast.LENGTH_SHORT).show();
                } else {
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "Checking user credentials for username: " + username);
                            User user = userDao.getUser(username, password);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (user != null) {
                                        Log.d(TAG, "User found: " + user.id);
                                        saveUserCredentials(username, password);
                                        Intent intent = new Intent(LoginDetailsActivity.this, InitialActivity.class);
                                        intent.putExtra("userId", user.id); // Pass the user ID to the next activity
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Log.d(TAG, "Invalid username or password");
                                        Toast.makeText(LoginDetailsActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
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

    private void saveUserCredentials(String username, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }
}
