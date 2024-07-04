package com.example.dreaminterpreterai;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private EditText dreamInput;
    private RecyclerView recyclerView;
    private Button interpretButton, backButton, newDreamButton;
    private ProgressBar progressBar;
    private AppDatabase db;
    private DreamDao dreamDao;
    private ExecutorService executorService;
    private List<ChatRequest.Message> conversationHistory;
    private MessageAdapter messageAdapter;
    private static final String TAG = "MainActivity";
    private static final String DISCLAIMER = "주의사항: 꿈 해석은 주관적이며 모든 사람에게 해당되지는 않을 수 있습니다. 아래 해석은 단순한 예측에 불과하니 유념하시기 바랍니다.";
    private static final int MAX_RETRIES = 3;
    private int userId;
    private int currentGroupId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dreamInput = findViewById(R.id.dreamInput);
        recyclerView = findViewById(R.id.recyclerView);
        interpretButton = findViewById(R.id.interpretButton);
        backButton = findViewById(R.id.backButton);
        newDreamButton = findViewById(R.id.newDreamButton);
        progressBar = findViewById(R.id.progressBar);

        db = AppDatabase.getInstance(this);
        dreamDao = db.dreamDao();
        executorService = Executors.newSingleThreadExecutor();
        conversationHistory = new ArrayList<>();
        messageAdapter = new MessageAdapter(conversationHistory);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", -1);

        interpretButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMessage = dreamInput.getText().toString();
                if (!userMessage.isEmpty()) {
                    addMessageToHistory("user", userMessage);
                    interpretDream(userMessage, 0);
                    dreamInput.setText(""); // Clear the input field
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InitialActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
            }
        });

        newDreamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewDream();
            }
        });

        // Initialize currentGroupId with the latest group ID from the database
        getCurrentGroupId();
    }

    private void startNewDream() {
        conversationHistory.clear();
        messageAdapter.notifyDataSetChanged();
        dreamInput.setText("");
        currentGroupId = (int) (System.currentTimeMillis() / 1000);  // Generate a new group ID
    }

    private void addMessageToHistory(String role, String content) {
        conversationHistory.add(new ChatRequest.Message(role, content));
        messageAdapter.notifyItemInserted(conversationHistory.size() - 1);
        recyclerView.scrollToPosition(conversationHistory.size() - 1);
    }

    private void interpretDream(String userMessage, int attempt) {
        String prompt;

        if (containsKoreanCharacters(userMessage)) {
            prompt = "당신은 꿈 해석가입니다. 다음 꿈을 해석하십시오: " + userMessage +
                    ". 해석된 결과를 섹션으로 나누어 예측하십시오. '물론입니다'라고 대답하지 말고 섹션으로 나눈 것만 대답해줘.";
        } else {
            prompt = "You are a dream interpreter. Interpret the following dream: " + userMessage +
                    ". Organize the interpretation into sections and give a prediction of the future.";
        }

        // Add the prompt message to the conversation history
        ChatRequest.Message promptMessage = new ChatRequest.Message("system", prompt);
        List<ChatRequest.Message> fullConversationHistory = new ArrayList<>(conversationHistory);
        fullConversationHistory.add(promptMessage);

        ChatRequest request = new ChatRequest("gpt-4", fullConversationHistory);

        ApiInterface apiService = ApiClient.getRetrofitInstance().create(ApiInterface.class);
        Call<ChatResponse> call = apiService.getDreamInterpretation(request);

        progressBar.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    String interpretation = response.body().getChoices().get(0).getMessage().getContent();
                    addMessageToHistory("assistant", interpretation);

                    // Save the dream details to the database
                    saveDream(userMessage, interpretation);
                } else {
                    Log.e(TAG, "Response code: " + response.code() + " Message: " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    addMessageToHistory("assistant", "Failed to interpret dream.");
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                if (attempt < MAX_RETRIES) {
                    Log.e(TAG, "Failure: " + t.getMessage() + ". Retrying... (" + (attempt + 1) + "/" + MAX_RETRIES + ")");
                    interpretDream(userMessage, attempt + 1);
                } else {
                    Log.e(TAG, "Failure: " + t.getMessage() + ". No more retries.");
                    if (t instanceof SocketTimeoutException) {
                        addMessageToHistory("assistant", "Error: Request timed out. Please try again.");
                    } else {
                        addMessageToHistory("assistant", "Error: " + t.getMessage());
                    }
                }
            }
        });
    }

    private void saveDream(String dream, String interpretation) {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Dream newDream = new Dream(currentDate, dream, interpretation, userId, currentGroupId);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                dreamDao.insert(newDream);
            }
        });
    }

    private boolean containsKoreanCharacters(String text) {
        return Pattern.compile("[ㄱ-ㅎㅏ-ㅣ가-힣]").matcher(text).find();
    }

    private void getCurrentGroupId() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<Dream> dreamList = dreamDao.getAllDreamsByUser(userId);
                if (dreamList != null && !dreamList.isEmpty()) {
                    currentGroupId = dreamList.get(0).groupId; // Get the latest group ID
                } else {
                    currentGroupId = (int) (System.currentTimeMillis() / 1000); // Initialize if no dreams exist
                }
            }
        });
    }
}
