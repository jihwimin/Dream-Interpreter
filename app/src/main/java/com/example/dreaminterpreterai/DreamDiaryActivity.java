package com.example.dreaminterpreterai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DreamDiaryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DreamAdapter dreamAdapter;
    private AppDatabase db;
    private DreamDao dreamDao;
    private ExecutorService executorService;
    private int userId;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream_diary);

        recyclerView = findViewById(R.id.recyclerView);
        backButton = findViewById(R.id.backButton);

        db = AppDatabase.getInstance(this);
        dreamDao = db.dreamDao();
        executorService = Executors.newSingleThreadExecutor();

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", -1);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadDreamsForUser();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DreamDiaryActivity.this, InitialActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadDreamsForUser() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<Dream> dreamList = dreamDao.getDreamsForUser(userId);
                List<Object> items = new ArrayList<>();
                int previousGroupId = -1;
                for (Dream dream : dreamList) {
                    if (dream.groupId != previousGroupId) {
                        if (previousGroupId != -1) {
                            items.add(new Object()); // Add a separator
                        }
                        previousGroupId = dream.groupId;
                    }
                    items.add(dream);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dreamAdapter = new DreamAdapter(items, new DreamAdapter.OnDreamDeleteListener() {
                            @Override
                            public void onDelete(Dream dream) {
                                deleteDream(dream);
                            }
                        });
                        recyclerView.setAdapter(dreamAdapter);
                    }
                });
            }
        });
    }

    private void deleteDream(Dream dream) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                dreamDao.delete(dream);
                loadDreamsForUser();
            }
        });
    }
}
