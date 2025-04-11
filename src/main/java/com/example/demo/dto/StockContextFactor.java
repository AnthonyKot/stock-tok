package com.example.demo.dto;

// Represents a single factor in the stock context analysis
public class StockContextFactor {
    private String factorName; // e.g., "Size Category", "Growth Stage"
    private String determination; // e.g., "Large-cap", "Mature"
    private String explanation; // The reasoning provided by Gemini

    // Getters and Setters (required by Jackson for deserialization)
    public String getFactorName() {
        return factorName;
    }

    public void setFactorName(String factorName) {
        this.factorName = factorName;
    }

    public String getDetermination() {
        return determination;
    }

    public void setDetermination(String determination) {
        this.determination = determination;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    // Optional: toString for basic debugging
    @Override
    public String toString() {
        return "StockContextFactor{" +
               "factorName='" + factorName +
               ", determination='" + determination +
               ", explanation='" + explanation +
               '}';
    }
}
