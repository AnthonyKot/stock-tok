# AI Integration Implementation Guide for Stock-Tok (30 ACU Budget)

This guide outlines how to enhance the existing Stock-Tok application with AI capabilities within a 30 ACU budget constraint. The implementation focuses on core features that provide the most value while leveraging the current Java + Angular architecture.

## Overview

Stock-Tok already provides basic AI-enhanced stock analysis using Google's Gemini API. This implementation guide will help you extend these capabilities with improved prompts, competitor analysis, and trend analysis, while also providing templates for UX improvements that can be implemented manually.

> **Note:** For detailed prompts to use with Gemini 2.5 Pro, see the [PROMPTS.md](./PROMPTS.md) file, which contains optimized prompts for financial analysis within our budget constraints. For features that exceed our current budget, see [FUTURE_WORK.md](./FUTURE_WORK.md).

## Current Architecture

Stock-Tok uses:
- **Spring Boot Backend**: Handles API requests and integrates with external services
- **Angular Frontend**: Provides the user interface
- **Google Gemini AI**: Generates contextual analysis for stocks
- **Alpha Vantage API**: Provides stock quote data

## Implementation Roadmap

The implementation is divided into two tracks:
1. **UX Improvements** - Templates for manual implementation (0 ACU)
2. **Core AI Analysis** - Features implemented with Gemini 2.5 Pro (30 ACU)

### Phase 1: UX Improvements (Templates for Manual Implementation)

These features can be implemented manually or with Gemini 2.0 Flash without using the 30 ACU budget.

#### 1.1 Responsive Design Template

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

#### 1.2 Historical Data Visualization Template

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

Create a chart component template:

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

#### 1.3 Save/Favorite Stocks Template

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
```:

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
## Frontend Integration

To display the AI analysis results in the frontend, create components for each analysis type:

### 1. Competitor Analysis Component

```typescript
// stock-ui/src/app/competitor-analysis/competitor-analysis.component.ts
import { Component, Input, OnInit } from '@angular/core';
import { StockApiService } from '../services/stock-api.service';

@Component({
  selector: 'app-competitor-analysis',
  templateUrl: './competitor-analysis.component.html',
  styleUrls: ['./competitor-analysis.component.css']
})
export class CompetitorAnalysisComponent implements OnInit {
  @Input() symbol: string = '';
  competitors: any = null;
  isLoading: boolean = false;
  error: string = '';
  
  constructor(private stockApiService: StockApiService) {}
  
  ngOnInit() {
    if (this.symbol) {
      this.loadCompetitors();
    }
  }
  
  loadCompetitors() {
    this.isLoading = true;
    this.competitors = null;
    this.error = '';
    
    this.stockApiService.getCompetitorAnalysis(this.symbol).subscribe({
      next: (data) => {
        this.competitors = data;
        this.isLoading = false;
      },
      error: (error) => {
        this.error = error.error || 'Error loading competitor analysis';
        this.isLoading = false;
      }
    });
  }
}
```

```html
<!-- stock-ui/src/app/competitor-analysis/competitor-analysis.component.html -->
<div class="competitor-container">
  <h4>Competitor Analysis</h4>
  
  <div *ngIf="isLoading" class="loading-indicator">
    Analyzing competitors...
  </div>
  
  <div *ngIf="error" class="error-message">
    {{ error }}
  </div>
  
  <div *ngIf="competitors && !isLoading" class="competitor-results">
    <div class="competitive-position">
      <h5>Competitive Position</h5>
      <p>{{ competitors.competitivePosition }}</p>
    </div>
    
    <div class="competitors-list">
      <div *ngFor="let competitor of competitors.competitors" class="competitor-card">
        <div class="competitor-header">
          <span class="competitor-name">{{ competitor.name }}</span>
          <span class="competitor-ticker">({{ competitor.ticker }})</span>
        </div>
        
        <div class="competitor-details">
          <div class="strengths">
            <h6>Strengths</h6>
            <ul>
              <li *ngFor="let strength of competitor.keyStrengths">{{ strength }}</li>
            </ul>
          </div>
          
          <div class="weaknesses">
            <h6>Weaknesses</h6>
            <ul>
              <li *ngFor="let weakness of competitor.keyWeaknesses">{{ weakness }}</li>
            </ul>
          </div>
          
          <div class="metrics">
            <h6>Key Metrics</h6>
            <div *ngFor="let metric of competitor.comparisonMetrics | keyvalue" class="metric">
              <span class="metric-name">{{ metric.key }}:</span>
              <span class="metric-value">{{ metric.value }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
```

### 2. Trend Analysis Component

```typescript
// stock-ui/src/app/trend-analysis/trend-analysis.component.ts
import { Component, Input, OnInit } from '@angular/core';
import { StockApiService } from '../services/stock-api.service';

@Component({
  selector: 'app-trend-analysis',
  templateUrl: './trend-analysis.component.html',
  styleUrls: ['./trend-analysis.component.css']
})
export class TrendAnalysisComponent implements OnInit {
  @Input() symbol: string = '';
  trends: any = null;
  isLoading: boolean = false;
  error: string = '';
  
  constructor(private stockApiService: StockApiService) {}
  
  ngOnInit() {
    if (this.symbol) {
      this.loadTrends();
    }
  }
  
  loadTrends() {
    this.isLoading = true;
    this.trends = null;
    this.error = '';
    
    this.stockApiService.getTrendAnalysis(this.symbol).subscribe({
      next: (data) => {
        this.trends = data;
        this.isLoading = false;
      },
      error: (error) => {
        this.error = error.error || 'Error loading trend analysis';
        this.isLoading = false;
      }
    });
  }
}
```

```html
<!-- stock-ui/src/app/trend-analysis/trend-analysis.component.html -->
<div class="trend-container">
  <h4>Financial Trend Analysis</h4>
  
  <div *ngIf="isLoading" class="loading-indicator">
    Analyzing trends...
  </div>
  
  <div *ngIf="error" class="error-message">
    {{ error }}
  </div>
  
  <div *ngIf="trends && !isLoading" class="trend-results">
    <div class="trend-summary">
      <h5>Overall Assessment</h5>
      <p>{{ trends.overallTrendAssessment }}</p>
    </div>
    
    <div class="trend-details">
      <div class="trend-card profitability">
        <h5>Profitability Trend</h5>
        <div class="trend-direction {{ trends.profitabilityTrend.direction }}">
          {{ trends.profitabilityTrend.direction | titlecase }}
        </div>
        <p>{{ trends.profitabilityTrend.analysis }}</p>
      </div>
      
      <div class="trend-card growth">
        <h5>Growth Trend</h5>
        <div class="trend-direction {{ trends.growthTrend.direction }}">
          {{ trends.growthTrend.direction | titlecase }}
        </div>
        <p>{{ trends.growthTrend.analysis }}</p>
      </div>
      
      <div class="trend-card financial-health">
        <h5>Financial Health Trend</h5>
        <div class="trend-direction {{ trends.financialHealthTrend.direction }}">
          {{ trends.financialHealthTrend.direction | titlecase }}
        </div>
        <p>{{ trends.financialHealthTrend.analysis }}</p>
      </div>
    </div>
  </div>
</div>
```

### 3. Update Stock API Service

```typescript
// stock-ui/src/app/services/stock-api.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class StockApiService {
  constructor(private http: HttpClient) {}
  
  getStockAnalysis(symbol: string): Observable<any> {
    return this.http.get<any>(`/api/analyze/${symbol}`);
  }
  
  getHistoricalData(symbol: string, timeframe: string = 'compact'): Observable<any> {
    return this.http.get<any>(`/api/analyze/historical/${symbol}?timeframe=${timeframe}`);
  }
  
  getCompetitorAnalysis(symbol: string): Observable<any> {
    return this.http.get<any>(`/api/analyze/competitors/${symbol}`);
  }
  
  getTrendAnalysis(symbol: string): Observable<any> {
    return this.http.get<any>(`/api/analyze/trends/${symbol}`);
  }
}
```

### 4. Update Main Component

```html
<!-- stock-ui/src/app/stock-analyzer/stock-analyzer.component.html -->
<!-- Add after the existing analysis results -->
<div class="advanced-analysis" *ngIf="analysisData">
  <app-competitor-analysis [symbol]="ticker"></app-competitor-analysis>
  <app-trend-analysis [symbol]="ticker"></app-trend-analysis>
</div>
```

## Cost Optimization Strategies

To maximize the value of the 30 ACU budget:

### 1. Response Caching

Implement caching to reduce redundant API calls:

```java
// src/main/java/com/example/demo/config/CacheConfig.java
package com.example.demo.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("contextAnalysis", "competitorAnalysis", "trendAnalysis");
    }
}
```

Update the service methods with caching:

```java
// src/main/java/com/example/demo/service/GeminiAnalysisService.java
@Cacheable(value = "contextAnalysis", key = "#ticker")
public StockContextAnalysis analyzeStockContext(String ticker) {
    // Implementation
}

@Cacheable(value = "competitorAnalysis", key = "#ticker")
public CompetitorAnalysis analyzeCompetitors(String ticker) {
    // Implementation
}

@Cacheable(value = "trendAnalysis", key = "#ticker")
public TrendAnalysis analyzeTrends(String ticker) {
    // Implementation
}
```

### 2. Prompt Optimization

Keep prompts concise to reduce token usage:

- Remove unnecessary fields from prompts
- Use structured output formats
- Limit the depth of analysis to essential information

### 3. Batch Processing

Pre-fetch data for popular stocks during low-usage periods:

```java
// src/main/java/com/example/demo/service/PreFetchService.java
package com.example.demo.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class PreFetchService {
    
    private final GeminiAnalysisService geminiAnalysisService;
    private final String[] popularStocks = {"AAPL", "MSFT", "GOOGL", "AMZN", "META"};
    
    public PreFetchService(GeminiAnalysisService geminiAnalysisService) {
        this.geminiAnalysisService = geminiAnalysisService;
    }
    
    @Scheduled(cron = "0 0 1 * * *") // Run at 1 AM every day
    public void preFetchPopularStocks() {
        for (String ticker : popularStocks) {
            try {
                geminiAnalysisService.analyzeStockContext(ticker);
                geminiAnalysisService.analyzeCompetitors(ticker);
                geminiAnalysisService.analyzeTrends(ticker);
            } catch (Exception e) {
                // Log error but continue with next stock
            }
        }
    }
}
```

## Conclusion

This implementation guide provides a focused roadmap for enhancing the Stock-Tok application with AI capabilities within a 30 ACU budget. By implementing the core AI analysis features (improved prompts, competitor analysis, and trend analysis) while providing templates for UX improvements, the application can deliver significant value to users without exceeding the budget constraints.

The guide emphasizes practical implementations that build upon the existing codebase, making it easier to integrate these enhancements without a complete rewrite. For features that exceed the current budget, refer to the [FUTURE_WORK.md](./FUTURE_WORK.md) document, which outlines additional enhancements that can be considered for future iterations.
