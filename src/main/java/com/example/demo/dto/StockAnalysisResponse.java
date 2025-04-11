package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonRawValue; // To include raw JSON string

// DTO to combine Alpha Vantage quote data and Gemini analysis
public class StockAnalysisResponse {

    // Keep quote data as raw JSON string for now for simplicity
    // Using @JsonRawValue tells Jackson to embed the string directly as JSON
    @JsonRawValue
    private String quoteData; 

    private StockContextAnalysis contextAnalysis;

    // Constructor
    public StockAnalysisResponse(String quoteData, StockContextAnalysis contextAnalysis) {
        this.quoteData = quoteData;
        this.contextAnalysis = contextAnalysis;
    }

    // Getters and Setters
    public String getQuoteData() {
        return quoteData;
    }

    public void setQuoteData(String quoteData) {
        this.quoteData = quoteData;
    }

    public StockContextAnalysis getContextAnalysis() {
        return contextAnalysis;
    }

    public void setContextAnalysis(StockContextAnalysis contextAnalysis) {
        this.contextAnalysis = contextAnalysis;
    }
}
