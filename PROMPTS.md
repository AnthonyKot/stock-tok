# Financial Analysis Prompts for Stock-Tok

This document provides optimized prompts for leveraging Google's Gemini 2.5 Pro to perform financial analysis in the Stock-Tok application. These prompts are designed to help users make informed financial decisions through AI-enhanced stock analysis.

## Core Analysis Prompts

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

### 2. Key Metrics Analysis

```json
{
  "role": "financial_analyst",
  "task": "Identify and analyze key metrics for {TICKER}",
  "context": "Company is a {SIZE_CATEGORY} in {INDUSTRY} sector at {GROWTH_STAGE} stage",
  "output_format": {
    "ticker": "{TICKER}",
    "metrics": [
      {
        "metricName": "<metric name>",
        "importance": "<high|medium|low>",
        "idealRange": "<ideal range for this company type>",
        "analysis": "<brief analysis of this metric for the company>"
      }
    ],
    "summary": "<overall assessment based on metrics>"
  }
}
```

### 3. Competitor Comparison

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

## Advanced Analysis Prompts

### 4. Trend Analysis

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
    "keyInflectionPoints": [
      {
        "period": "<time period>",
        "event": "<significant change or event>",
        "impact": "<impact on financial performance>"
      }
    ],
    "overallTrendAssessment": "<synthesis of all trend analyses>"
  }
}
```

### 5. Investment Hypothesis

```json
{
  "role": "financial_analyst",
  "task": "Generate investment hypotheses for {TICKER}",
  "output_format": {
    "ticker": "{TICKER}",
    "hypotheses": [
      {
        "statement": "<clear, testable hypothesis>",
        "rationale": "<economic or business logic>",
        "testingApproach": "<how to test using historical data>",
        "implicationsIfTrue": "<what this means for investors>"
      }
    ]
  }
}
```

### 6. Predictive Analysis

```json
{
  "role": "financial_analyst",
  "task": "Predict future performance for {TICKER} based on historical data",
  "output_format": {
    "ticker": "{TICKER}",
    "shortTermOutlook": {
      "timeframe": "Next 12 months",
      "revenueGrowthPrediction": "<prediction with confidence level>",
      "marginTrendPrediction": "<prediction with confidence level>",
      "keyRisks": ["<risk 1>", "<risk 2>"]
    },
    "mediumTermOutlook": {
      "timeframe": "1-3 years",
      "growthTrajectory": "<prediction with confidence level>",
      "competitivePositionChange": "<prediction with confidence level>",
      "keyOpportunities": ["<opportunity 1>", "<opportunity 2>"]
    },
    "predictionConfidence": "<overall confidence assessment>",
    "keyAssumptions": ["<assumption 1>", "<assumption 2>"]
  }
}
```

## Company Type-Specific Prompts

### 7. High-Growth Tech Company Analysis

```json
{
  "role": "financial_analyst",
  "task": "Analyze {TICKER} as a high-growth tech company",
  "output_format": {
    "ticker": "{TICKER}",
    "growthSustainability": {
      "assessment": "<sustainable|concerning|unsustainable>",
      "keyMetrics": {
        "revenueGrowthRate": "<value>",
        "userGrowthRate": "<value>",
        "netRetentionRate": "<value>"
      },
      "analysis": "<analysis of growth sustainability>"
    },
    "pathToProfitability": {
      "assessment": "<clear|uncertain|concerning>",
      "estimatedTimeframe": "<timeframe>",
      "keyMetrics": {
        "grossMargin": "<value>",
        "operatingLossPercent": "<value>",
        "burnRate": "<value>"
      },
      "analysis": "<analysis of path to profitability>"
    },
    "unitEconomics": {
      "assessment": "<strong|improving|concerning|weak>",
      "keyMetrics": {
        "ltv": "<value>",
        "cac": "<value>",
        "ltvCacRatio": "<value>",
        "paybackPeriod": "<value>"
      },
      "analysis": "<analysis of unit economics>"
    }
  }
}
```

### 8. Mature Dividend Company Analysis

```json
{
  "role": "financial_analyst",
  "task": "Analyze {TICKER} as a mature dividend company",
  "output_format": {
    "ticker": "{TICKER}",
    "dividendSustainability": {
      "assessment": "<very secure|secure|concerning|at risk>",
      "keyMetrics": {
        "dividendYield": "<value>",
        "payoutRatio": "<value>",
        "dividendGrowthRate": "<value>",
        "yearsOfDividendGrowth": "<value>"
      },
      "analysis": "<analysis of dividend sustainability>"
    },
    "cashGenerationCapability": {
      "assessment": "<strong|stable|declining|weak>",
      "keyMetrics": {
        "freeCashFlow": "<value>",
        "fcfYield": "<value>",
        "fcfToNetIncome": "<value>"
      },
      "analysis": "<analysis of cash generation capability>"
    },
    "competitivePosition": {
      "assessment": "<strong|stable|eroding>",
      "keyMetrics": {
        "marketShare": "<value>",
        "marginTrend": "<trend>",
        "returnOnCapital": "<value>"
      },
      "analysis": "<analysis of competitive position>"
    }
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

@Value("${gemini.prompt.metrics.json}")
private String metricsPromptTemplate;

// Additional prompt templates...

public StockContextAnalysis analyzeStockContext(String ticker) {
    // Existing implementation with updated prompts
}

// Add new methods for additional analysis types
public CompetitorAnalysis analyzeCompetitors(String ticker) {
    // Implementation using competitor comparison prompt
}

public TrendAnalysis analyzeTrends(String ticker) {
    // Implementation using trend analysis prompt
}

// Additional analysis methods...
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

// Additional DTOs for other analysis types...
```

3. **Extend Controller**: Add endpoints for the new analysis types:

```java
// Example new endpoints in StockAnalysisController.java
@GetMapping("/competitors/{symbol}")
public ResponseEntity<?> getCompetitorAnalysis(@PathVariable String symbol) {
    // Implementation
}

@GetMapping("/trends/{symbol}")
public ResponseEntity<?> getTrendAnalysis(@PathVariable String symbol) {
    // Implementation
}

// Additional endpoints...
```

4. **Frontend Integration**: Create components to display the new analysis types:

```typescript
// Example component for competitor analysis
@Component({
  selector: 'app-competitor-analysis',
  templateUrl: './competitor-analysis.component.html'
})
export class CompetitorAnalysisComponent {
  @Input() symbol: string;
  competitors: any;
  
  // Implementation
}

// Additional components for other analysis types...
```

## Feedback Loop Implementation

To implement a feedback loop for continuous improvement:

1. **Track Prediction Accuracy**:
   - Store predictions made by the system
   - Compare with actual results when available
   - Calculate accuracy metrics

2. **Hypothesis Refinement**:
   - Use the accuracy data to refine prompts
   - Adjust weightings of different factors
   - Eliminate or modify underperforming metrics

3. **User Feedback Integration**:
   - Allow users to rate analysis quality
   - Collect specific feedback on what was helpful or misleading
   - Use this feedback to improve prompts

4. **Periodic Review**:
   - Regularly review and update prompts based on:
     - Market changes
     - New financial metrics
     - Emerging analysis techniques

By implementing these prompts and the feedback loop, Stock-Tok can provide increasingly accurate and valuable financial analysis to help users make informed investment decisions.
