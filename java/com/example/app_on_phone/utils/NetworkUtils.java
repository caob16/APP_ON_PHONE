package com.example.app_on_phone.utils;
import android.util.Log;

import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class NetworkUtils {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client;
    public NetworkUtils() {
        this.client = new OkHttpClient();
    }

    public CompletableFuture<String> sendRequest(String url, String jsonParameters) {
        RequestBody body = RequestBody.create(jsonParameters, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        return CompletableFuture.supplyAsync(() -> {
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                return response.body().string();
            } catch (IOException e) {
                throw new RuntimeException("Request failed", e);
            }
        });
    }
}
