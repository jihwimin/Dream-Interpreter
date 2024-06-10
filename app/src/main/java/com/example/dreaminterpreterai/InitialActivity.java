package com.example.dreaminterpreterai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class InitialActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        Button dreamInterpreterButton = findViewById(R.id.dreamInterpreterButton);
        dreamInterpreterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity
                Intent intent = new Intent(InitialActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
