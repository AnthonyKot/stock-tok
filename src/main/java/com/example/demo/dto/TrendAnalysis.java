package com.example.demo.dto;

/**
 * Data Transfer Object for trend analysis results.
 * Implementation Complexity: 3/10
 * 
 * This DTO is relatively simple:
 * - Basic nested class structure
 * - Standard Java bean patterns
 * - No complex collections
 */
public class TrendAnalysis {
    private String ticker;
    private TrendDirection profitabilityTrend;
    private TrendDirection growthTrend;
    private TrendDirection financialHealthTrend;
    private String overallTrendAssessment;
    
    public TrendAnalysis() {
    }
    
    public String getTicker() {
        return ticker;
    }
    
    public void setTicker(String ticker) {
        this.ticker = ticker;
    }
    
    public TrendDirection getProfitabilityTrend() {
        return profitabilityTrend;
    }
    
    public void setProfitabilityTrend(TrendDirection profitabilityTrend) {
        this.profitabilityTrend = profitabilityTrend;
    }
    
    public TrendDirection getGrowthTrend() {
        return growthTrend;
    }
    
    public void setGrowthTrend(TrendDirection growthTrend) {
        this.growthTrend = growthTrend;
    }
    
    public TrendDirection getFinancialHealthTrend() {
        return financialHealthTrend;
    }
    
    public void setFinancialHealthTrend(TrendDirection financialHealthTrend) {
        this.financialHealthTrend = financialHealthTrend;
    }
    
    public String getOverallTrendAssessment() {
        return overallTrendAssessment;
    }
    
    public void setOverallTrendAssessment(String overallTrendAssessment) {
        this.overallTrendAssessment = overallTrendAssessment;
    }
    
    /**
     * Nested class representing a trend direction with analysis.
     */
    public static class TrendDirection {
        private String direction;
        private String analysis;
        
        public TrendDirection() {
        }
        
        public String getDirection() {
            return direction;
        }
        
        public void setDirection(String direction) {
            this.direction = direction;
        }
        
        public String getAnalysis() {
            return analysis;
        }
        
        public void setAnalysis(String analysis) {
            this.analysis = analysis;
        }
    }
}
