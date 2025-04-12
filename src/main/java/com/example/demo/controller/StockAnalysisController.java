package com.example.demo.controller;

import com.example.demo.dto.CompetitorAnalysis;
import com.example.demo.dto.StockAnalysisResponse;
import com.example.demo.dto.StockContextAnalysis;
import com.example.demo.dto.TrendAnalysis;
import com.example.demo.service.CompetitorAnalysisService;
import com.example.demo.service.GeminiAnalysisService;
import com.example.demo.service.StockDataService;
import com.example.demo.service.TrendAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Controller for stock analysis endpoints.
 * Implementation Complexity: 5/10
 * 
 * This controller is moderately complex:
 * - Multiple endpoints and services
 * - Error handling
 * - Parameter validation
 * - Response entity construction
 */
@RestController
@RequestMapping("/api/analyze") 
// Allow requests from Angular dev server (default port 4200)
@CrossOrigin(origins = "http://localhost:4200") 
public class StockAnalysisController {

    @Autowired
    private StockDataService stockDataService;

    @Autowired
    private GeminiAnalysisService geminiAnalysisService;
    
    @Autowired
    private CompetitorAnalysisService competitorAnalysisService;
    
    @Autowired
    private TrendAnalysisService trendAnalysisService;

    /**
     * Main endpoint for basic stock analysis.
     * 
     * @param symbol Stock ticker symbol
     * @return Combined stock data and context analysis
     */
    @GetMapping("/{symbol}")
    public ResponseEntity<?> getStockAnalysis(@PathVariable String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Stock symbol cannot be empty.");
        }

        String ticker = symbol.trim().toUpperCase();

        try {
            System.out.println("Controller: Fetching quote for " + ticker);
            String quoteJson = stockDataService.getStockQuote(ticker);

            System.out.println("Controller: Fetching analysis for " + ticker);
            StockContextAnalysis analysisResult = geminiAnalysisService.analyzeStockContext(ticker);

            StockAnalysisResponse response = new StockAnalysisResponse(quoteJson, analysisResult);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            System.err.println("Controller Error for symbol " + ticker + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                           .body("Error fetching or analyzing stock data: " + e.getMessage());
        } catch (Exception e) {
             System.err.println("Controller Unexpected Error for symbol " + ticker + ": " + e.getMessage());
             e.printStackTrace(); 
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("An unexpected error occurred: " + e.getMessage());
        }
    }
    
    /**
     * Endpoint for historical stock data.
     * Implementation Complexity: 4/10
     * 
     * @param symbol Stock ticker symbol
     * @param timeframe Data timeframe (compact or full)
     * @return Historical price data
     */
    @GetMapping("/historical/{symbol}")
    public ResponseEntity<?> getHistoricalData(@PathVariable String symbol, 
                                              @RequestParam(defaultValue = "compact") String timeframe) {
        if (symbol == null || symbol.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Stock symbol cannot be empty.");
        }

        String ticker = symbol.trim().toUpperCase();

        try {
            
            return ResponseEntity.ok("{}");
        } catch (Exception e) {
            System.err.println("Historical Data Error for symbol " + ticker + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                       .body("Error fetching historical data: " + e.getMessage());
        }
    }
    
    /**
     * Endpoint for competitor analysis.
     * Implementation Complexity: 6/10
     * 
     * @param symbol Stock ticker symbol
     * @return Competitor analysis data
     */
    @GetMapping("/competitors/{symbol}")
    public ResponseEntity<?> getCompetitorAnalysis(@PathVariable String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Stock symbol cannot be empty.");
        }

        String ticker = symbol.trim().toUpperCase();

        try {
            System.out.println("Controller: Fetching competitor analysis for " + ticker);
            CompetitorAnalysis analysis = competitorAnalysisService.analyzeCompetitors(ticker);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            System.err.println("Competitor Analysis Error for symbol " + ticker + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                       .body("Error analyzing competitors: " + e.getMessage());
        }
    }
    
    /**
     * Endpoint for trend analysis.
     * Implementation Complexity: 6/10
     * 
     * @param symbol Stock ticker symbol
     * @return Trend analysis data
     */
    @GetMapping("/trends/{symbol}")
    public ResponseEntity<?> getTrendAnalysis(@PathVariable String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Stock symbol cannot be empty.");
        }

        String ticker = symbol.trim().toUpperCase();

        try {
            System.out.println("Controller: Fetching trend analysis for " + ticker);
            TrendAnalysis analysis = trendAnalysisService.analyzeTrends(ticker);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            System.err.println("Trend Analysis Error for symbol " + ticker + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                       .body("Error analyzing trends: " + e.getMessage());
        }
    }
}
