package com.example.dreaminterpreterai;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Collections;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private EditText dreamInput;
    private TextView interpretationOutput;
    private Button interpretButton;

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
        ChatRequest.Message userMessage = new ChatRequest.Message("user", dream);
        ChatRequest request = new ChatRequest("gpt-3.5-turbo", Collections.singletonList(userMessage));

        ApiInterface apiService = ApiClient.getRetrofitInstance().create(ApiInterface.class);
        Call<ChatResponse> call = apiService.getDreamInterpretation(request);

        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String interpretation = response.body().getChoices().get(0).getMessage().getContent();
                    interpretationOutput.setText(interpretation);
                } else {
                    interpretationOutput.setText("Failed to interpret dream.");
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                interpretationOutput.setText("Error: " + t.getMessage());
            }
        });
    }
}
