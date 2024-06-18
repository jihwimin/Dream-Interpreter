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
    private static final String TAG = "InitialActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial); // Ensure the correct layout file is used

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", -1);
        Log.d(TAG, "Received userId: " + userId);

        Button dreamInterpreterButton = findViewById(R.id.dreamInterpreterButton);
        dreamInterpreterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialActivity.this, MainActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        Button dreamDiaryButton = findViewById(R.id.dreamDiaryButton);
        dreamDiaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialActivity.this, DreamDiaryActivity.class);
                intent.putExtra("userId", userId);
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
