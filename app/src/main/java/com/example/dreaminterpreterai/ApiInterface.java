package com.example.dreaminterpreterai;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {
    @Headers({
            "Content-Type: application/json",
            "Authorization: Bearer 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"
    })
    @POST("chat/completions")
    Call<ChatResponse> getDreamInterpretation(@Body ChatRequest request);
}
