package com.example.dreaminterpreterai;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class InitialActivity extends AppCompatActivity {
    private int userId;
    private String username; // Add username field
    private static final String TAG = "InitialActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", -1);
        username = intent.getStringExtra("username"); // Get username from intent
        Log.d(TAG, "Received userId: " + userId + " username: " + username);

        Button dreamInterpreterButton = findViewById(R.id.dreamInterpreterButton);
        dreamInterpreterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialActivity.this, MainActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("username", username); // Pass username
                startActivity(intent);
            }
        });

        Button dreamDiaryButton = findViewById(R.id.dreamDiaryButton);
        dreamDiaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialActivity.this, DreamDiaryActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("username", username); // Pass username
                startActivity(intent);
            }
        });

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear the saved user credentials
        editor.apply();

        Intent intent = new Intent(InitialActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close the current activity
    }
}
