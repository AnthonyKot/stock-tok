package com.example.demo.service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.HttpUrl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class StockDataService {

    @Value("${alpha.vantage.api.key}") // Inject key from application.properties
    private String alphaVantageApiKey;

    private final OkHttpClient httpClient = new OkHttpClient();
    // private final ObjectMapper objectMapper = new ObjectMapper(); // For parsing JSON later

    public String getStockQuote(String symbol) throws IOException {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Stock symbol cannot be empty.");
        }
        if (alphaVantageApiKey == null || alphaVantageApiKey.trim().isEmpty()) {
            throw new IllegalStateException("Alpha Vantage API key is not configured.");
        }

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("www.alphavantage.co")
                .encodedPath("/query")
                .addQueryParameter("function", "GLOBAL_QUOTE")
                .addQueryParameter("symbol", symbol)
                .addQueryParameter("apikey", alphaVantageApiKey)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get() // Explicitly state GET method
                .build();

        System.out.println("Requesting URL: " + url); // Log the request URL for debugging

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("API Request Failed: " + response.code() + " - " + response.message());
                if (response.body() != null) {
                     System.err.println("Response Body: " + response.body().string());
                }
                throw new IOException("Unexpected code " + response);
            }
            // For now, just return the raw JSON response body
            if (response.body() != null) {
                return response.body().string();
            } else {
                return "{}"; // Return empty JSON object if body is null
            }
        } catch (IOException e) {
            System.err.println("Error fetching stock data for " + symbol + ": " + e.getMessage());
            throw e; // Re-throw the exception
        }
    }

    // We can add methods to parse the JSON later using Jackson (objectMapper)
    // e.g., public StockQuote parseQuote(String jsonResponse) { ... }
}
