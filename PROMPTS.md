# Financial Analysis Prompts for Stock-Tok (30 ACU Budget Implementation)

This document provides optimized prompts for leveraging Google's Gemini 2.5 Pro to perform financial analysis in the Stock-Tok application. These prompts are specifically selected to fit within the 30 ACU budget constraint while providing maximum value.

> **Note:** For additional prompts that could be implemented in future iterations, see the [FUTURE_WORK.md](./FUTURE_WORK.md) file.

## Core Analysis Prompts (5 ACU)

### 1. Company Context Analysis

```json
{
  "role": "financial_analyst",
  "task": "Analyze the context of {TICKER}",
  "output_format": {
    "ticker": "{TICKER}",
    "factors": [
      {
        "factorName": "Size Category",
        "determination": "<large-cap|mid-cap|small-cap|micro-cap>",
        "explanation": "<explanation with market cap figures>"
      },
      {
        "factorName": "Growth Stage",
        "determination": "<early-stage|growth|mature|declining>",
        "explanation": "<explanation with growth metrics>"
      },
      {
        "factorName": "Industry Position",
        "determination": "<leader|challenger|follower>",
        "explanation": "<explanation of market position>"
      },
      {
        "factorName": "Financial Health",
        "determination": "<strong|stable|concerning|weak>",
        "explanation": "<explanation with key financial ratios>"
      }
    ]
  }
}
```

## Competitor Analysis Prompt (10 ACU)

### 2. Competitor Comparison

```json
{
  "role": "financial_analyst",
  "task": "Compare {TICKER} with its key competitors",
  "output_format": {
    "ticker": "{TICKER}",
    "competitors": [
      {
        "name": "<competitor name>",
        "ticker": "<competitor ticker>",
        "keyStrengths": ["<strength 1>", "<strength 2>"],
        "keyWeaknesses": ["<weakness 1>", "<weakness 2>"],
        "comparisonMetrics": {
          "<metric 1>": "<comparison result>",
          "<metric 2>": "<comparison result>"
        }
      }
    ],
    "competitivePosition": "<overall competitive position assessment>"
  }
}
```

## Trend Analysis Prompt (15 ACU)

### 3. Basic Trend Analysis

```json
{
  "role": "financial_analyst",
  "task": "Analyze financial trends for {TICKER} over the past 3 years",
  "output_format": {
    "ticker": "{TICKER}",
    "profitabilityTrend": {
      "direction": "<improving|stable|declining>",
      "analysis": "<analysis of profitability metrics over time>"
    },
    "growthTrend": {
      "direction": "<accelerating|stable|decelerating>",
      "analysis": "<analysis of growth metrics over time>"
    },
    "financialHealthTrend": {
      "direction": "<strengthening|stable|weakening>",
      "analysis": "<analysis of financial health metrics over time>"
    },
    "overallTrendAssessment": "<synthesis of all trend analyses>"
  }
}
```

## Implementation in Stock-Tok

To implement these prompts in the Stock-Tok application:

1. **Update GeminiAnalysisService**: Modify the existing service to use these structured prompts:

```java
// Example implementation in GeminiAnalysisService.java
@Value("${gemini.prompt.context.json}")
private String contextPromptTemplate;

@Value("${gemini.prompt.competitor.json}")
private String competitorPromptTemplate;

@Value("${gemini.prompt.trend.json}")
private String trendPromptTemplate;

public StockContextAnalysis analyzeStockContext(String ticker) {
    // Implementation using context analysis prompt
    String prompt = contextPromptTemplate.replace("{TICKER}", ticker);
    String response = callGeminiApi(prompt);
    return parseContextAnalysisResponse(response);
}

public CompetitorAnalysis analyzeCompetitors(String ticker) {
    // Implementation using competitor comparison prompt
    String prompt = competitorPromptTemplate.replace("{TICKER}", ticker);
    String response = callGeminiApi(prompt);
    return parseCompetitorAnalysisResponse(response);
}

public TrendAnalysis analyzeTrends(String ticker) {
    // Implementation using trend analysis prompt
    String prompt = trendPromptTemplate.replace("{TICKER}", ticker);
    String response = callGeminiApi(prompt);
    return parseTrendAnalysisResponse(response);
}
```

2. **Create New DTOs**: Define data transfer objects for each analysis type:

```java
// Example DTO for competitor analysis
public class CompetitorAnalysis {
    private String ticker;
    private List<Competitor> competitors;
    private String competitivePosition;
    
    // Nested classes, getters, setters...
}

// Example DTO for trend analysis
public class TrendAnalysis {
    private String ticker;
    private TrendDirection profitabilityTrend;
    private TrendDirection growthTrend;
    private TrendDirection financialHealthTrend;
    private String overallTrendAssessment;
    
    // Nested classes, getters, setters...
}
```

3. **Extend Controller**: Add endpoints for the new analysis types:

```java
// Example new endpoints in StockAnalysisController.java
@GetMapping("/competitors/{symbol}")
public ResponseEntity<?> getCompetitorAnalysis(@PathVariable String symbol) {
    if (symbol == null || symbol.trim().isEmpty()) {
        return ResponseEntity.badRequest().body("Stock symbol cannot be empty.");
    }

    String ticker = symbol.trim().toUpperCase();

    try {
        CompetitorAnalysis analysis = geminiAnalysisService.analyzeCompetitors(ticker);
        return ResponseEntity.ok(analysis);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                       .body("Error analyzing competitors: " + e.getMessage());
    }
}

@GetMapping("/trends/{symbol}")
public ResponseEntity<?> getTrendAnalysis(@PathVariable String symbol) {
    if (symbol == null || symbol.trim().isEmpty()) {
        return ResponseEntity.badRequest().body("Stock symbol cannot be empty.");
    }

    String ticker = symbol.trim().toUpperCase();

    try {
        TrendAnalysis analysis = geminiAnalysisService.analyzeTrends(ticker);
        return ResponseEntity.ok(analysis);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                       .body("Error analyzing trends: " + e.getMessage());
    }
}
```

## Cost Optimization Strategies

To maximize the value of the 30 ACU budget:

1. **Response Caching**:
   ```java
   // Example caching implementation
   @Cacheable(value = "competitorAnalysis", key = "#ticker")
   public CompetitorAnalysis analyzeCompetitors(String ticker) {
       // Implementation
   }
   ```

2. **Prompt Optimization**:
   - Keep prompts concise while maintaining quality
   - Use structured output formats to reduce token usage
   - Remove unnecessary fields from response templates

3. **Batch Processing**:
   - Pre-fetch and cache data for popular stocks
   - Combine multiple analysis requests where possible

By implementing these optimized prompts and cost-saving strategies, Stock-Tok can provide valuable financial analysis within the 30 ACU budget constraint.
