package com.example.dreaminterpreterai;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private EditText dreamInput;
    private TextView interpretationOutput;
    private Button interpretButton, backButton;
    private ProgressBar progressBar;
    private AppDatabase db;
    private DreamDao dreamDao;
    private ExecutorService executorService;
    private static final String TAG = "MainActivity";
    private static final String DISCLAIMER = "주의사항: 꿈 해석은 주관적이며 모든 사람에게 해당되지는 않을 수 있습니다. 아래 해석은 단순한 예측에 불과하니 유념하시기 바랍니다.";
    private static final int MAX_RETRIES = 3;
    private int userId; // Add userId field

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dreamInput = findViewById(R.id.dreamInput);
        interpretationOutput = findViewById(R.id.interpretationOutput);
        interpretButton = findViewById(R.id.interpretButton);
        backButton = findViewById(R.id.backButton);
        progressBar = findViewById(R.id.progressBar);

        db = AppDatabase.getInstance(this);
        dreamDao = db.dreamDao();
        executorService = Executors.newSingleThreadExecutor();

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", -1); // Get user ID from intent

        interpretButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interpretDream(0); // Initial attempt
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InitialActivity.class);
                intent.putExtra("userId", userId); // Pass user ID back if needed
                startActivity(intent);
                finish();
            }
        });
    }

    private void interpretDream(int attempt) {
        String dream = dreamInput.getText().toString();
        String prompt;

        if (containsKoreanCharacters(dream)) {
            prompt = "당신은 꿈 해석가입니다. 다음 꿈을 해석하십시오: " + dream +
                    ". 해석된 결과를 섹션으로 나누어 예측하십시오. '물론입니다'라고 대답하지 말고 섹션으로 나눈 것만 대답해줘.";
        } else {
            prompt = "You are a dream interpreter. Interpret the following dream: " + dream +
                    ". Organize the interpretation into sections and give a prediction of the future.";
        }


        ChatRequest.Message userMessage = new ChatRequest.Message("user", prompt);
        ChatRequest request = new ChatRequest("gpt-4o", Collections.singletonList(userMessage));  // Using GPT-4

        ApiInterface apiService = ApiClient.getRetrofitInstance().create(ApiInterface.class);
        Call<ChatResponse> call = apiService.getDreamInterpretation(request);

        progressBar.setVisibility(View.VISIBLE);
        interpretationOutput.setText(""); // Clear previous output

        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    String interpretation = response.body().getChoices().get(0).getMessage().getContent();
                    String fullMessage = DISCLAIMER + "\n\n" + interpretation;
                    interpretationOutput.setText(fullMessage);

                    // Save the dream details to the database
                    saveDream(dream, fullMessage);
                } else {
                    Log.e(TAG, "Response code: " + response.code() + " Message: " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    interpretationOutput.setText(DISCLAIMER + "\n\nFailed to interpret dream.");
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                if (attempt < MAX_RETRIES) {
                    Log.e(TAG, "Failure: " + t.getMessage() + ". Retrying... (" + (attempt + 1) + "/" + MAX_RETRIES + ")");
                    interpretDream(attempt + 1); // Retry
                } else {
                    Log.e(TAG, "Failure: " + t.getMessage() + ". No more retries.");
                    if (t instanceof SocketTimeoutException) {
                        interpretationOutput.setText(DISCLAIMER + "\n\nError: Request timed out. Please try again.");
                    } else {
                        interpretationOutput.setText(DISCLAIMER + "\n\nError: " + t.getMessage());
                    }
                }
            }
        });
    }

    private void saveDream(String dream, String interpretation) {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Dream newDream = new Dream(currentDate, dream, interpretation, userId); // Include userId
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
}
