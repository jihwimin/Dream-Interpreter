package com.example.dreaminterpreterai;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DreamDiaryActivity extends AppCompatActivity implements DreamAdapter.OnDreamDeleteListener {
    private AppDatabase db;
    private DreamDao dreamDao;
    private RecyclerView recyclerView;
    private DreamAdapter adapter;
    private ExecutorService executorService;
    private int userId;
    private static final String TAG = "DreamDiaryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream_diary);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = AppDatabase.getInstance(this);
        dreamDao = db.dreamDao();
        executorService = Executors.newSingleThreadExecutor();

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", -1);
        Log.d(TAG, "Received userId: " + userId);

        loadUserDreams();

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(DreamDiaryActivity.this, InitialActivity.class);
                backIntent.putExtra("userId", userId); // Pass user ID back if needed
                startActivity(backIntent);
                finish();
            }
        });
    }

    private void loadUserDreams() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<Dream> dreamList = dreamDao.getDreamsByUserId(userId); // Fetch dreams for the current user
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new DreamAdapter(dreamList, DreamDiaryActivity.this);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        });
    }

    @Override
    public void onDelete(Dream dream) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                dreamDao.delete(dream);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadUserDreams(); // Reload dreams after deletion
                    }
                });
            }
        });
    }
}
