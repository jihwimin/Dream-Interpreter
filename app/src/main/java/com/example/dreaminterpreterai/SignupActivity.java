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

public class SignupActivity extends AppCompatActivity {

    private static final Pattern ENGLISH_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");
    private AppDatabase db;
    private UserDao userDao;
    private ExecutorService executorService;
    private static final String TAG = "SignupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        EditText signupUsernameEditText = findViewById(R.id.signupUsernameEditText);
        EditText signupEmailEditText = findViewById(R.id.signupEmailEditText);
        EditText signupPasswordEditText = findViewById(R.id.signupPasswordEditText);
        Button signupSubmitButton = findViewById(R.id.signupSubmitButton);
        Button backButton = findViewById(R.id.backButton);

        db = AppDatabase.getInstance(this);
        userDao = db.userDao();
        executorService = Executors.newSingleThreadExecutor();

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
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "Checking if username exists: " + username);
                            User existingUser = userDao.getUserByUsername(username);
                            if (existingUser != null) {
                                Log.d(TAG, "Username already exists: " + username);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SignupActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Log.d(TAG, "Creating new user");
                                User user = new User(username, email, password);
                                userDao.insert(user);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        saveUserCredentials(username, password);
                                        Intent intent = new Intent(SignupActivity.this, InitialActivity.class);
                                        intent.putExtra("userId", user.id); // Pass the user ID to the next activity
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        }
                    });
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

    private void saveUserCredentials(String username, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }
}
