package com.example.demo.dto;

import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for competitor analysis results.
 * Implementation Complexity: 4/10
 * 
 * This DTO is moderately complex:
 * - Nested class structure
 * - Multiple collection types
 * - Standard Java bean patterns
 */
public class CompetitorAnalysis {
    private String ticker;
    private List<Competitor> competitors;
    private String competitivePosition;
    
    public CompetitorAnalysis() {
    }
    
    public String getTicker() {
        return ticker;
    }
    
    public void setTicker(String ticker) {
        this.ticker = ticker;
    }
    
    public List<Competitor> getCompetitors() {
        return competitors;
    }
    
    public void setCompetitors(List<Competitor> competitors) {
        this.competitors = competitors;
    }
    
    public String getCompetitivePosition() {
        return competitivePosition;
    }
    
    public void setCompetitivePosition(String competitivePosition) {
        this.competitivePosition = competitivePosition;
    }
    
    /**
     * Nested class representing a competitor company.
     */
    public static class Competitor {
        private String name;
        private String ticker;
        private List<String> keyStrengths;
        private List<String> keyWeaknesses;
        private Map<String, String> comparisonMetrics;
        
        public Competitor() {
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getTicker() {
            return ticker;
        }
        
        public void setTicker(String ticker) {
            this.ticker = ticker;
        }
        
        public List<String> getKeyStrengths() {
            return keyStrengths;
        }
        
        public void setKeyStrengths(List<String> keyStrengths) {
            this.keyStrengths = keyStrengths;
        }
        
        public List<String> getKeyWeaknesses() {
            return keyWeaknesses;
        }
        
        public void setKeyWeaknesses(List<String> keyWeaknesses) {
            this.keyWeaknesses = keyWeaknesses;
        }
        
        public Map<String, String> getComparisonMetrics() {
            return comparisonMetrics;
        }
        
        public void setComparisonMetrics(Map<String, String> comparisonMetrics) {
            this.comparisonMetrics = comparisonMetrics;
        }
    }
}
