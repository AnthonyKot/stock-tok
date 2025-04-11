package com.example.demo.dto;

import java.util.List;

// Represents the overall analysis structure containing multiple factors
public class StockContextAnalysis {
    private String ticker;
    private List<StockContextFactor> factors;

    // Getters and Setters (required by Jackson for deserialization)
    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public List<StockContextFactor> getFactors() {
        return factors;
    }

    public void setFactors(List<StockContextFactor> factors) {
        this.factors = factors;
    }

    // Optional: toString for basic debugging
    @Override
    public String toString() {
        return "StockContextAnalysis{" +
               "ticker='" + ticker +
               ", factors=" + factors +
               '}';
    }
}
