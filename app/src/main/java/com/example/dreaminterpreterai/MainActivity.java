package com.example.dreaminterpreterai;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;
import java.util.regex.Pattern;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private EditText dreamInput;
    private TextView interpretationOutput;
    private Button interpretButton;
    private static final String TAG = "MainActivity";

    private String apiKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dreamInput = findViewById(R.id.dreamInput);
        interpretationOutput = findViewById(R.id.interpretationOutput);
        interpretButton = findViewById(R.id.interpretButton);

        interpretButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interpretDream();
            }
        });
    }

    private void interpretDream() {
        String dream = dreamInput.getText().toString();
        String prompt;

        if (containsKoreanCharacters(dream)) {
            prompt = "당신은 꿈 해석가입니다. 다음 꿈을 해석하십시오: " + dream +
                    ". 해석된 결과를 섹션으로 나누어 예측하십시오.";
        } else {
            prompt = "You are a dream interpreter. Interpret the following dream: " + dream +
                    ". Organize the interpretation into sections and give a prediction of the future.";
        }
        ChatRequest.Message userMessage = new ChatRequest.Message("user", prompt);
        ChatRequest request = new ChatRequest("gpt-3.5-turbo", Collections.singletonList(userMessage));

        ApiInterface apiService = ApiClient.getRetrofitInstance(apiKey).create(ApiInterface.class);
        Call<ChatResponse> call = apiService.getDreamInterpretation(request);

        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String interpretation = response.body().getChoices().get(0).getMessage().getContent();
                    interpretationOutput.setText(interpretation);
                } else {
                    Log.e(TAG, "Response code: " + response.code() + " Message: " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    interpretationOutput.setText("Failed to interpret dream.");
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                Log.e(TAG, "Failure: " + t.getMessage());
                interpretationOutput.setText("Error: " + t.getMessage());
            }
        });
    }
    private boolean containsKoreanCharacters(String text) {
        return Pattern.compile("[ㄱ-ㅎㅏ-ㅣ가-힣]").matcher(text).find();
    }
    private void loadApiKey() {
        Properties properties = new Properties();
        try {
            InputStream inputStream = getAssets().open("config.properties");
            properties.load(inputStream);
            apiKey = properties.getProperty("API_KEY");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
