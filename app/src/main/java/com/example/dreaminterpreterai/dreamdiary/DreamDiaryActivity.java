package com.example.dreaminterpreterai.dreamdiary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dreaminterpreterai.DreamDatabase;
import com.example.dreaminterpreterai.InitialActivity;
import com.example.dreaminterpreterai.R;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DreamDiaryActivity extends AppCompatActivity implements DreamAdapter.OnDreamDeleteListener {
    private DreamDatabase db;
    private DreamDao dreamDao;
    private RecyclerView recyclerView;
    private DreamAdapter adapter;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream_diary);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = DreamDatabase.getInstance(this);
        dreamDao = db.dreamDao();
        executorService = Executors.newSingleThreadExecutor();

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DreamDiaryActivity.this, InitialActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loadDreams();
    }

    private void loadDreams() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final List<Dream> dreamList = dreamDao.getAllDreams();
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
                        loadDreams();
                    }
                });
            }
        });
    }
}
