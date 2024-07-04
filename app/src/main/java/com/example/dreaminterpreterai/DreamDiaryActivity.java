package com.example.dreaminterpreterai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DreamDiaryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DreamDao dreamDao;
    private ExecutorService executorService;
    private int userId;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream_diary);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        backButton = findViewById(R.id.backButton);

        AppDatabase db = AppDatabase.getInstance(this);
        dreamDao = db.dreamDao();
        executorService = Executors.newSingleThreadExecutor();

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", -1);

        loadDreams();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DreamDiaryActivity.this, InitialActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish(); // Close the current activity
            }
        });
    }

    private void loadDreams() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<Dream> dreamList = dreamDao.getAllDreamsByUser(userId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DreamAdapter adapter = new DreamAdapter(dreamList, new DreamAdapter.OnDreamDeleteListener() {
                            @Override
                            public void onDelete(Dream dream) {
                                deleteDream(dream);
                            }
                        });
                        recyclerView.setAdapter(adapter);
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
                loadDreams(); // Refresh the list after deletion
            }
        });
    }
}
