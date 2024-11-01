package com.example.dreaminterpreterai;

import java.util.concurrent.TimeUnit;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://api.openai.com/v1/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);

            // Set timeout settings
            httpClient.connectTimeout(30, TimeUnit.SECONDS); // 30 seconds connect timeout
            httpClient.readTimeout(30, TimeUnit.SECONDS);    // 30 seconds read timeout
            httpClient.writeTimeout(30, TimeUnit.SECONDS);   // 30 seconds write timeout

            // Read the API key from the environment variable
            //Dotenv dotenv = Dotenv.configure().load();
            //String apiKey = dotenv.get("OPENAI_API_KEY");
            String apiKey = "API KEY";
            //String apiKey = System.getenv("OPENAI_API_KEY");
            if (apiKey == null || apiKey.isEmpty()) {
                throw new IllegalStateException("API key is not set in the environment variables.");
            }
            httpClient.addInterceptor(chain -> {
                return chain.proceed(chain.request().newBuilder()
                        .header("Authorization", "Bearer " + apiKey)
                        .build());
            });

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }
}
