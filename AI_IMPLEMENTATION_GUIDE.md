# AI Integration Implementation Guide for Stock-Tok

This guide outlines how to enhance the existing Stock-Tok application with advanced AI capabilities, focusing on user experience improvements while leveraging the current Java + Angular architecture.

## Overview

Stock-Tok already provides basic AI-enhanced stock analysis using Google's Gemini API. This implementation guide will help you extend these capabilities with more advanced features like predictive modeling, historical backtesting, and improved visualizations.

## Current Architecture

Stock-Tok uses:
- **Spring Boot Backend**: Handles API requests and integrates with external services
- **Angular Frontend**: Provides the user interface
- **Google Gemini AI**: Generates contextual analysis for stocks
- **Alpha Vantage API**: Provides stock quote data

## Implementation Roadmap

### Phase 1: Enhanced User Experience (High Priority)

#### 1.1 Responsive Design Implementation

```typescript
// stock-ui/src/styles.css
/* Add responsive breakpoints */
@media (max-width: 768px) {
  .analysis-results {
    flex-direction: column;
  }
  
  .quote-data, .gemini-analysis {
    width: 100%;
    margin-bottom: 20px;
  }
}

/* Add to existing styles */
.analysis-results {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.quote-data, .gemini-analysis {
  flex: 1;
  min-width: 300px;
}
```

#### 1.2 Dark Mode Implementation

```typescript
// stock-ui/src/app/app.component.ts
import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  isDarkMode = false;

  toggleDarkMode() {
    this.isDarkMode = !this.isDarkMode;
    document.body.classList.toggle('dark-theme', this.isDarkMode);
    localStorage.setItem('darkMode', this.isDarkMode.toString());
  }

  ngOnInit() {
    const savedMode = localStorage.getItem('darkMode');
    if (savedMode === 'true') {
      this.isDarkMode = true;
      document.body.classList.add('dark-theme');
    }
  }
}
```

```html
<!-- stock-ui/src/app/app.component.html -->
<div class="theme-toggle">
  <button (click)="toggleDarkMode()">
    {{ isDarkMode ? '☀️ Light Mode' : '🌙 Dark Mode' }}
  </button>
</div>
```

```css
/* stock-ui/src/styles.css */
:root {
  --bg-color: #ffffff;
  --text-color: #333333;
  --card-bg: #f5f5f5;
  --border-color: #dddddd;
  --primary-color: #3f51b5;
}

body.dark-theme {
  --bg-color: #121212;
  --text-color: #e0e0e0;
  --card-bg: #1e1e1e;
  --border-color: #333333;
  --primary-color: #7986cb;
}

body {
  background-color: var(--bg-color);
  color: var(--text-color);
  transition: background-color 0.3s, color 0.3s;
}

.analysis-results {
  background-color: var(--card-bg);
  border: 1px solid var(--border-color);
}
```

#### 1.3 Historical Data Visualization

First, update the backend to fetch historical data:

```java
// src/main/java/com/example/demo/service/StockDataService.java
public String getHistoricalData(String symbol, String timeframe) throws IOException {
    if (symbol == null || symbol.trim().isEmpty()) {
        throw new IllegalArgumentException("Stock symbol cannot be empty.");
    }
    
    HttpUrl url = new HttpUrl.Builder()
            .scheme("https")
            .host("www.alphavantage.co")
            .encodedPath("/query")
            .addQueryParameter("function", "TIME_SERIES_DAILY")
            .addQueryParameter("symbol", symbol)
            .addQueryParameter("outputsize", timeframe.equals("full") ? "full" : "compact")
            .addQueryParameter("apikey", alphaVantageApiKey)
            .build();
    
    Request request = new Request.Builder()
            .url(url)
            .get()
            .build();
    
    try (Response response = httpClient.newCall(request).execute()) {
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        if (response.body() != null) {
            return response.body().string();
        } else {
            return "{}";
        }
    }
}
```

Add a new endpoint to the controller:

```java
// src/main/java/com/example/demo/controller/StockAnalysisController.java
@GetMapping("/historical/{symbol}")
public ResponseEntity<?> getHistoricalData(@PathVariable String symbol, 
                                          @RequestParam(defaultValue = "compact") String timeframe) {
    if (symbol == null || symbol.trim().isEmpty()) {
        return ResponseEntity.badRequest().body("Stock symbol cannot be empty.");
    }

    String ticker = symbol.trim().toUpperCase();

    try {
        String historicalData = stockDataService.getHistoricalData(ticker, timeframe);
        return ResponseEntity.ok(historicalData);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                       .body("Error fetching historical data: " + e.getMessage());
    }
}
```

Add Chart.js to the Angular project:

```bash
cd stock-ui
npm install chart.js
```

Create a chart component:

```typescript
// stock-ui/src/app/stock-chart/stock-chart.component.ts
import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-stock-chart',
  templateUrl: './stock-chart.component.html',
  styleUrls: ['./stock-chart.component.css']
})
export class StockChartComponent implements OnChanges {
  @Input() historicalData: any;
  @Input() symbol: string = '';
  chart: any;

  ngOnChanges(changes: SimpleChanges) {
    if (changes['historicalData'] && this.historicalData) {
      this.createChart();
    }
  }

  createChart() {
    if (this.chart) {
      this.chart.destroy();
    }

    const timeSeriesData = this.historicalData['Time Series (Daily)'];
    if (!timeSeriesData) return;

    const dates = Object.keys(timeSeriesData).sort();
    const prices = dates.map(date => parseFloat(timeSeriesData[date]['4. close']));

    const canvas = document.getElementById('stockChart') as HTMLCanvasElement;
    this.chart = new Chart(canvas, {
      type: 'line',
      data: {
        labels: dates,
        datasets: [{
          label: `${this.symbol} Price`,
          data: prices,
          borderColor: 'rgb(75, 192, 192)',
          tension: 0.1
        }]
      },
      options: {
        responsive: true,
        scales: {
          x: {
            display: true,
            title: {
              display: true,
              text: 'Date'
            }
          },
          y: {
            display: true,
            title: {
              display: true,
              text: 'Price ($)'
            }
          }
        }
      }
    });
  }
}
```

```html
<!-- stock-ui/src/app/stock-chart/stock-chart.component.html -->
<div class="chart-container">
  <canvas id="stockChart"></canvas>
</div>
```

Update the stock analyzer component:

```typescript
// stock-ui/src/app/stock-analyzer/stock-analyzer.component.ts
// Add to existing imports
import { StockApiService } from '../services/stock-api.service';

// Add to component class
historicalData: any = null;
isLoadingHistorical: boolean = false;

// Update analyzeStock method
analyzeStock() {
  this.errorMessage = '';
  this.isLoading = true;
  this.analysisData = null;
  this.historicalData = null;
  
  if (!this.ticker) {
    this.errorMessage = 'Please enter a stock symbol';
    this.isLoading = false;
    return;
  }
  
  this.stockApiService.getStockAnalysis(this.ticker).subscribe({
    next: (data) => {
      this.analysisData = data;
      this.isLoading = false;
      this.loadHistoricalData();
    },
    error: (error) => {
      this.errorMessage = error.error || 'An error occurred while analyzing the stock';
      this.isLoading = false;
    }
  });
}

// Add new method
loadHistoricalData() {
  this.isLoadingHistorical = true;
  
  this.stockApiService.getHistoricalData(this.ticker).subscribe({
    next: (data) => {
      this.historicalData = data;
      this.isLoadingHistorical = false;
    },
    error: (error) => {
      console.error('Error loading historical data:', error);
      this.isLoadingHistorical = false;
    }
  });
}
```

Update the stock API service:

```typescript
// stock-ui/src/app/services/stock-api.service.ts
// Add to service class
getHistoricalData(symbol: string, timeframe: string = 'compact'): Observable<any> {
  return this.http.get<any>(`/api/analyze/historical/${symbol}?timeframe=${timeframe}`);
}
```

Update the stock analyzer template:

```html
<!-- stock-ui/src/app/stock-analyzer/stock-analyzer.component.html -->
<!-- Add after the existing analysis results -->
<div *ngIf="historicalData && !isLoadingHistorical" class="historical-chart">
  <h4>Historical Price Chart</h4>
  <app-stock-chart [historicalData]="historicalData" [symbol]="ticker"></app-stock-chart>
</div>

<div *ngIf="isLoadingHistorical" class="loading-indicator">
  Loading historical data...
</div>
```

#### 1.4 Save/Favorite Stocks Feature

Add a service for managing favorites:

```typescript
// stock-ui/src/app/services/favorites.service.ts
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FavoritesService {
  private favorites: string[] = [];
  private favoritesSubject = new BehaviorSubject<string[]>([]);
  
  favorites$ = this.favoritesSubject.asObservable();
  
  constructor() {
    this.loadFavorites();
  }
  
  private loadFavorites() {
    const saved = localStorage.getItem('favoriteStocks');
    if (saved) {
      this.favorites = JSON.parse(saved);
      this.favoritesSubject.next([...this.favorites]);
    }
  }
  
  private saveFavorites() {
    localStorage.setItem('favoriteStocks', JSON.stringify(this.favorites));
    this.favoritesSubject.next([...this.favorites]);
  }
  
  addFavorite(symbol: string) {
    if (!this.favorites.includes(symbol)) {
      this.favorites.push(symbol);
      this.saveFavorites();
    }
  }
  
  removeFavorite(symbol: string) {
    const index = this.favorites.indexOf(symbol);
    if (index !== -1) {
      this.favorites.splice(index, 1);
      this.saveFavorites();
    }
  }
  
  isFavorite(symbol: string): boolean {
    return this.favorites.includes(symbol);
  }
}
```

Update the stock analyzer component:

```typescript
// stock-ui/src/app/stock-analyzer/stock-analyzer.component.ts
// Add to imports
import { FavoritesService } from '../services/favorites.service';

// Add to constructor
constructor(
  private stockApiService: StockApiService,
  private favoritesService: FavoritesService
) {}

// Add methods
isFavorite(): boolean {
  return this.ticker ? this.favoritesService.isFavorite(this.ticker) : false;
}

toggleFavorite() {
  if (!this.ticker) return;
  
  if (this.isFavorite()) {
    this.favoritesService.removeFavorite(this.ticker);
  } else {
    this.favoritesService.addFavorite(this.ticker);
  }
}

// Add property
favoriteStocks: string[] = [];

// Add to ngOnInit
ngOnInit() {
  this.favoritesService.favorites$.subscribe(favorites => {
    this.favoriteStocks = favorites;
  });
}
```

Update the template:

```html
<!-- stock-ui/src/app/stock-analyzer/stock-analyzer.component.html -->
<!-- Add near the top, after the input group -->
<div class="favorites-container">
  <div class="favorite-stocks">
    <h4>Favorite Stocks</h4>
    <div class="favorite-chips">
      <span *ngFor="let stock of favoriteStocks" 
            class="favorite-chip"
            (click)="ticker = stock; analyzeStock()">
        {{ stock }}
      </span>
      <span *ngIf="favoriteStocks.length === 0" class="no-favorites">
        No favorites yet
      </span>
    </div>
  </div>
</div>

<!-- Add near the ticker input -->
<button *ngIf="ticker" 
        (click)="toggleFavorite()" 
        class="favorite-button"
        [class.is-favorite]="isFavorite()">
  {{ isFavorite() ? '★ Remove from Favorites' : '☆ Add to Favorites' }}
</button>
```

Add styles:

```css
/* stock-ui/src/app/stock-analyzer/stock-analyzer.component.css */
.favorites-container {
  margin: 20px 0;
}

.favorite-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.favorite-chip {
  background-color: var(--primary-color);
  color: white;
  padding: 5px 10px;
  border-radius: 20px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.favorite-chip:hover {
  background-color: var(--primary-color-dark);
}

.favorite-button {
  margin-left: 10px;
  background-color: transparent;
  border: 1px solid var(--primary-color);
  color: var(--primary-color);
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
}

.favorite-button.is-favorite {
  background-color: var(--primary-color);
  color: white;
}

.no-favorites {
  font-style: italic;
  color: var(--text-color-secondary);
}
```

### Phase 2: AI Enhancements

#### 2.1 Improved Gemini Prompt for Better Analysis

Update the Gemini prompt to provide more detailed analysis:

```java
// src/main/resources/prompts.properties
gemini.prompt.context.json={\
  "role": "financial_analyst",\
  "task": "Analyze the stock {TICKER} and provide detailed insights",\
  "output_format": {\
    "ticker": "{TICKER}",\
    "factors": [\
      {\
        "factorName": "Size Category",\
        "determination": "<large-cap|mid-cap|small-cap|micro-cap>",\
        "explanation": "<detailed explanation with market cap figures>"\
      },\
      {\
        "factorName": "Growth Stage",\
        "determination": "<early-stage|growth|mature|declining>",\
        "explanation": "<detailed explanation with growth metrics>"\
      },\
      {\
        "factorName": "Sector Performance",\
        "determination": "<outperforming|in-line|underperforming>",\
        "explanation": "<detailed explanation comparing to sector>"\
      },\
      {\
        "factorName": "Volatility Assessment",\
        "determination": "<high|moderate|low>",\
        "explanation": "<detailed explanation with beta and historical volatility>"\
      },\
      {\
        "factorName": "Dividend Profile",\
        "determination": "<high-yield|growth-focused|balanced|non-dividend>",\
        "explanation": "<detailed explanation of dividend history and policy>"\
      },\
      {\
        "factorName": "Key Risks",\
        "determination": "<high|moderate|low>",\
        "explanation": "<detailed explanation of main risk factors>"\
      },\
      {\
        "factorName": "Future Outlook",\
        "determination": "<positive|neutral|negative>",\
        "explanation": "<detailed explanation of future prospects>"\
      }\
    ]\
  }\
}
```

#### 2.2 Simple Predictive Analysis

Add a basic predictive analysis service:

```java
// src/main/java/com/example/demo/service/PredictiveAnalysisService.java
package com.example.demo.service;

import com.example.demo.dto.PredictionResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PredictiveAnalysisService {
    
    private final StockDataService stockDataService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public PredictiveAnalysisService(StockDataService stockDataService) {
        this.stockDataService = stockDataService;
    }
    
    public PredictionResult predictTrend(String symbol) throws IOException {
        String historicalData = stockDataService.getHistoricalData(symbol, "compact");
        JsonNode rootNode = objectMapper.readTree(historicalData);
        
        if (!rootNode.has("Time Series (Daily)")) {
            throw new IOException("Invalid historical data format");
        }
        
        JsonNode timeSeriesNode = rootNode.get("Time Series (Daily)");
        List<Double> closePrices = new ArrayList<>();
        List<String> dates = new ArrayList<>();
        
        timeSeriesNode.fieldNames().forEachRemaining(dates::add);
        dates.sort(String::compareTo);
        
        for (String date : dates) {
            double closePrice = timeSeriesNode.get(date).get("4. close").asDouble();
            closePrices.add(closePrice);
        }
        
        // Simple moving average calculation
        int period = 5;
        double[] sma = calculateSMA(closePrices, period);
        
        // Simple trend prediction based on last few SMAs
        String trendPrediction = "neutral";
        String confidence = "low";
        
        if (sma.length >= 3) {
            double lastSMA = sma[sma.length - 1];
            double secondLastSMA = sma[sma.length - 2];
            double thirdLastSMA = sma[sma.length - 3];
            
            if (lastSMA > secondLastSMA && secondLastSMA > thirdLastSMA) {
                trendPrediction = "upward";
                confidence = calculateConfidence(lastSMA, secondLastSMA, thirdLastSMA);
            } else if (lastSMA < secondLastSMA && secondLastSMA < thirdLastSMA) {
                trendPrediction = "downward";
                confidence = calculateConfidence(lastSMA, secondLastSMA, thirdLastSMA);
            }
        }
        
        return new PredictionResult(symbol, trendPrediction, confidence, 
                                   "Based on simple moving average analysis of recent price action");
    }
    
    private double[] calculateSMA(List<Double> prices, int period) {
        if (prices.size() < period) {
            return new double[0];
        }
        
        double[] sma = new double[prices.size() - period + 1];
        
        for (int i = 0; i <= prices.size() - period; i++) {
            double sum = 0;
            for (int j = i; j < i + period; j++) {
                sum += prices.get(j);
            }
            sma[i] = sum / period;
        }
        
        return sma;
    }
    
    private String calculateConfidence(double last, double secondLast, double thirdLast) {
        double change1 = Math.abs((last - secondLast) / secondLast);
        double change2 = Math.abs((secondLast - thirdLast) / thirdLast);
        
        if (change1 > 0.02 && change2 > 0.02) {
            return "high";
        } else if (change1 > 0.01 && change2 > 0.01) {
            return "medium";
        } else {
            return "low";
        }
    }
}
```

Create a DTO for prediction results:

```java
// src/main/java/com/example/demo/dto/PredictionResult.java
package com.example.demo.dto;

public class PredictionResult {
    private String symbol;
    private String trendPrediction;
    private String confidence;
    private String explanation;
    
    public PredictionResult() {}
    
    public PredictionResult(String symbol, String trendPrediction, String confidence, String explanation) {
        this.symbol = symbol;
        this.trendPrediction = trendPrediction;
        this.confidence = confidence;
        this.explanation = explanation;
    }
    
    // Getters and setters
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public String getTrendPrediction() {
        return trendPrediction;
    }
    
    public void setTrendPrediction(String trendPrediction) {
        this.trendPrediction = trendPrediction;
    }
    
    public String getConfidence() {
        return confidence;
    }
    
    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }
    
    public String getExplanation() {
        return explanation;
    }
    
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
```

Add an endpoint to the controller:

```java
// src/main/java/com/example/demo/controller/StockAnalysisController.java
// Add to class
@Autowired
private PredictiveAnalysisService predictiveAnalysisService;

// Add new endpoint
@GetMapping("/predict/{symbol}")
public ResponseEntity<?> getPrediction(@PathVariable String symbol) {
    if (symbol == null || symbol.trim().isEmpty()) {
        return ResponseEntity.badRequest().body("Stock symbol cannot be empty.");
    }

    String ticker = symbol.trim().toUpperCase();

    try {
        PredictionResult prediction = predictiveAnalysisService.predictTrend(ticker);
        return ResponseEntity.ok(prediction);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                       .body("Error generating prediction: " + e.getMessage());
    }
}
```

Update the Angular service:

```typescript
// stock-ui/src/app/services/stock-api.service.ts
// Add to service class
getPrediction(symbol: string): Observable<any> {
  return this.http.get<any>(`/api/analyze/predict/${symbol}`);
}
```

Create a prediction component:

```typescript
// stock-ui/src/app/prediction/prediction.component.ts
import { Component, Input, OnChanges } from '@angular/core';
import { StockApiService } from '../services/stock-api.service';

@Component({
  selector: 'app-prediction',
  templateUrl: './prediction.component.html',
  styleUrls: ['./prediction.component.css']
})
export class PredictionComponent implements OnChanges {
  @Input() symbol: string = '';
  prediction: any = null;
  isLoading: boolean = false;
  error: string = '';
  
  constructor(private stockApiService: StockApiService) {}
  
  ngOnChanges() {
    if (this.symbol) {
      this.loadPrediction();
    }
  }
  
  loadPrediction() {
    this.isLoading = true;
    this.prediction = null;
    this.error = '';
    
    this.stockApiService.getPrediction(this.symbol).subscribe({
      next: (data) => {
        this.prediction = data;
        this.isLoading = false;
      },
      error: (error) => {
        this.error = error.error || 'Error loading prediction';
        this.isLoading = false;
      }
    });
  }
}
```

```html
<!-- stock-ui/src/app/prediction/prediction.component.html -->
<div class="prediction-container">
  <h4>Price Trend Prediction</h4>
  
  <div *ngIf="isLoading" class="loading-indicator">
    Generating prediction...
  </div>
  
  <div *ngIf="error" class="error-message">
    {{ error }}
  </div>
  
  <div *ngIf="prediction && !isLoading" class="prediction-result">
    <div class="prediction-header">
      <span class="prediction-symbol">{{ prediction.symbol }}</span>
      <span class="prediction-trend" [ngClass]="prediction.trendPrediction">
        {{ prediction.trendPrediction | titlecase }} Trend
      </span>
      <span class="prediction-confidence">
        {{ prediction.confidence | titlecase }} Confidence
      </span>
    </div>
    
    <div class="prediction-explanation">
      <p>{{ prediction.explanation }}</p>
      <p class="prediction-disclaimer">
        Note: This is a simple prediction based on recent price movements and should not be used as financial advice.
      </p>
    </div>
  </div>
</div>
```

```css
/* stock-ui/src/app/prediction/prediction.component.css */
.prediction-container {
  margin-top: 20px;
  padding: 15px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  background-color: var(--card-bg);
}

.prediction-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.prediction-symbol {
  font-weight: bold;
  font-size: 1.2em;
  margin-right: 15px;
}

.prediction-trend {
  padding: 5px 10px;
  border-radius: 4px;
  margin-right: 15px;
}

.prediction-trend.upward {
  background-color: #4caf50;
  color: white;
}

.prediction-trend.downward {
  background-color: #f44336;
  color: white;
}

.prediction-trend.neutral {
  background-color: #ff9800;
  color: white;
}

.prediction-confidence {
  font-style: italic;
}

.prediction-explanation {
  line-height: 1.5;
}

.prediction-disclaimer {
  font-size: 0.9em;
  font-style: italic;
  color: var(--text-color-secondary);
  margin-top: 15px;
}
```

Update the stock analyzer component to include the prediction:

```html
<!-- stock-ui/src/app/stock-analyzer/stock-analyzer.component.html -->
<!-- Add after the historical chart -->
<app-prediction *ngIf="analysisData" [symbol]="ticker"></app-prediction>
```

### Phase 3: Advanced Features (Future Implementation)

For future implementation, consider these more advanced features:

1. **Competitor Analysis**:
   - Integrate with a company information API to identify competitors
   - Use Gemini to compare companies in the same sector

2. **Portfolio Analysis**:
   - Allow users to create and save portfolios
   - Provide aggregate analysis across all stocks in a portfolio

3. **News Integration**:
   - Fetch relevant news articles about analyzed stocks
   - Use Gemini to summarize news sentiment

4. **Advanced Backtesting**:
   - Implement a more sophisticated backtesting system
   - Allow users to test different investment strategies

## Conclusion

This implementation guide provides a roadmap for enhancing the Stock-Tok application with improved user experience and AI capabilities. By following the phases outlined above, you can transform the application into a more powerful and user-friendly tool for stock analysis.

The guide focuses on practical implementations that build upon the existing codebase, making it easier to integrate these enhancements without a complete rewrite. The emphasis on user experience improvements aligns with the priority identified by the project stakeholders.
