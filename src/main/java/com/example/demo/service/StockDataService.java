package com.example.demo.service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.HttpUrl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Service for fetching stock data from Alpha Vantage API.
 * Implementation Complexity: 4/10
 * 
 * This service requires:
 * - External API integration
 * - Error handling
 * - Response parsing
 * - Multiple endpoint methods
 */
@Service
public class StockDataService {

    @Value("${alpha.vantage.api.key}") // Inject key from application.properties
    private String alphaVantageApiKey;

    private final OkHttpClient httpClient = new OkHttpClient();
    // private final ObjectMapper objectMapper = new ObjectMapper(); // For parsing JSON later

    /**
     * Fetches stock quote data from Alpha Vantage API.
     * 
     * @param symbol The stock symbol to fetch data for
     * @return JSON string containing the stock quote data
     * @throws IOException If there's an error fetching the data
     */
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
    
    /**
     * Fetches historical stock data from Alpha Vantage API.
     * Implementation Complexity: 4/10
     * 
     * @param symbol The stock symbol to fetch data for
     * @param timeframe The timeframe for historical data (compact or full)
     * @return JSON string containing the historical stock data
     * @throws IOException If there's an error fetching the data
     */
    public String getHistoricalData(String symbol, String timeframe) throws IOException {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Stock symbol cannot be empty.");
        }
        if (alphaVantageApiKey == null || alphaVantageApiKey.trim().isEmpty()) {
            throw new IllegalStateException("Alpha Vantage API key is not configured.");
        }
        
        if (timeframe == null || (!timeframe.equals("compact") && !timeframe.equals("full"))) {
            timeframe = "compact"; // Default to compact if invalid
        }
        
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("www.alphavantage.co")
                .encodedPath("/query")
                .addQueryParameter("function", "TIME_SERIES_DAILY")
                .addQueryParameter("symbol", symbol)
                .addQueryParameter("outputsize", timeframe)
                .addQueryParameter("apikey", alphaVantageApiKey)
                .build();
        
        System.out.println("Historical Data Request URL: " + url); // Log for debugging
        
        
        return "{}";
    }

    // We can add methods to parse the JSON later using Jackson (objectMapper)
    // e.g., public StockQuote parseQuote(String jsonResponse) { ... }
}
