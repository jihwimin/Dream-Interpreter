package com.example.dreaminterpreterai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dreaminterpreterai.dreamdiary.DreamDiaryActivity;

public class InitialActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        Button dreamInterpreterButton = findViewById(R.id.dreamInterpreterButton);
        Button dreamDiaryButton = findViewById(R.id.dreamDiaryButton);

        dreamInterpreterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        dreamDiaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialActivity.this, DreamDiaryActivity.class);
                startActivity(intent);
            }
        });
    }
}
