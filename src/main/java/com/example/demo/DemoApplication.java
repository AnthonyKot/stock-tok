package com.example.demo;

// Removed DTO imports as pretty printing is also removed
// import com.example.demo.dto.StockContextAnalysis;
// import com.example.demo.dto.StockContextFactor;

// Keep service imports if needed elsewhere, otherwise remove
// import com.example.demo.service.StockDataService;
// import com.example.demo.service.GeminiAnalysisService;

// Removed CommandLineRunner imports 
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.context.annotation.Bean;
// import org.springframework.core.env.Environment;
// import org.springframework.beans.factory.annotation.Autowired;
// import java.io.IOException;
// import java.util.Objects;
// import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:messages.properties") // Keep if messages are used elsewhere
public class DemoApplication {

    // Removed Environment and Service injections as they are no longer used here
    // @Autowired
    // private Environment env;
    // @Autowired
    // private StockDataService stockDataService;
    // @Autowired
    // private GeminiAnalysisService geminiAnalysisService;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        System.out.println("Stock Analysis API Started. Access via /api/analyze/{symbol}"); // Optional startup message
    }

    // REMOVED the CommandLineRunner bean and the prettyPrintAnalysis method
    /*
    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            // ... removed command line logic ...
        };
    }

    private void prettyPrintAnalysis(StockContextAnalysis analysis) {
        // ... removed pretty print logic ...
    }
    */

}
