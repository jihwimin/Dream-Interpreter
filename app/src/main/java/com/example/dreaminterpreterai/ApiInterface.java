package com.example.dreaminterpreterai;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/*
public interface ApiInterface {
    @Headers({
            "Content-Type: application/json",
            "Authorization: Bearer API KEY"
    })
    @POST("chat/completions")
    Call<ChatResponse> getDreamInterpretation(@Body ChatRequest request);
}
 */

public interface ApiInterface {
    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    Call<ChatResponse> getDreamInterpretation(@Body ChatRequest request);
}

