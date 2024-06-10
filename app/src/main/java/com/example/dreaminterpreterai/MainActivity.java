package com.example.dreaminterpreterai;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Collections;
import java.util.regex.Pattern;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.ImageView;
import android.widget.LinearLayout;
public class MainActivity extends AppCompatActivity {
    private EditText dreamInput;
    private TextView interpretationOutput;
    private Button interpretButton;
    private static final String TAG = "MainActivity";

    private static final String DISCLAIMER = "주의사항: 꿈 해석은 주관적이며 모든 사람에게 해당되지는 않을 수 있습니다. 아래 해석은 단순한 예측에 불과하니 유념하시기 바랍니다.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dreamInput = findViewById(R.id.dreamInput);
        interpretationOutput = findViewById(R.id.interpretationOutput);
        interpretButton = findViewById(R.id.interpretButton);

        //Interpret Dream BUttom
        interpretButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interpretDream();
            }
        });

        //Back Button
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to InitialActivity
                Intent intent = new Intent(MainActivity.this, InitialActivity.class);
                startActivity(intent);
                finish(); // Optionally call finish() to close MainActivity
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

        ApiInterface apiService = ApiClient.getRetrofitInstance().create(ApiInterface.class);
        Call<ChatResponse> call = apiService.getDreamInterpretation(request);

        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String interpretation = response.body().getChoices().get(0).getMessage().getContent();
                    String fullMessage = DISCLAIMER + "\n\n" + interpretation;
                    interpretationOutput.setText(fullMessage);
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
                Log.e(TAG, "Failure: " + t.getMessage());
                interpretationOutput.setText("Error: " + t.getMessage());
            }
        });
    }

    private boolean containsKoreanCharacters(String text) {
        return Pattern.compile("[ㄱ-ㅎㅏ-ㅣ가-힣]").matcher(text).find();
    }

}
