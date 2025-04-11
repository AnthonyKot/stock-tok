package com.example.demo.controller;

import com.example.demo.dto.StockAnalysisResponse;
import com.example.demo.dto.StockContextAnalysis;
import com.example.demo.service.StockDataService;
import com.example.demo.service.GeminiAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin; // Import CrossOrigin
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/analyze") 
// Allow requests from Angular dev server (default port 4200)
@CrossOrigin(origins = "http://localhost:4200") 
public class StockAnalysisController {

    @Autowired
    private StockDataService stockDataService;

    @Autowired
    private GeminiAnalysisService geminiAnalysisService;

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
}
