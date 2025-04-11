# Stock-Tok Implementation Budget Estimates

This document provides a focused implementation plan for enhancing Stock-Tok with only the most critical features within a limited budget of 30-50 ACUs and a 1-week timeframe. The plan prioritizes core AI enhancements while providing templates for UX improvements that can be completed with minimal AI assistance.

## MVP Implementation Plan

### Phase 1: UX Improvements (3 days)

| Task | Developer Days | ACU Cost | Priority | Implementation |
|------|---------------|----------|----------|----------------|
| Responsive Design Implementation | 1 | 0 | High | Manual/Template |
| Historical Data Visualization (Basic Chart) | 1 | 0 | High | Manual/Template |
| Save/Favorite Stocks Feature | 0.5 | 0 | High | Manual/Template |
| **Subtotal** | **2.5** | **0** | | |

### Phase 2: Core AI Analysis (3 days)

| Task | Developer Days | ACU Cost | Priority | Implementation |
|------|---------------|----------|----------|----------------|
| Improved Gemini Prompts Implementation | 1 | 5 | High | Gemini 2.5 Pro |
| Competitor Analysis Feature | 1 | 10 | High | Gemini 2.5 Pro |
| Basic Trend Analysis | 1 | 15 | High | Gemini 2.5 Pro |
| **Subtotal** | **3** | **30** | | |

## Total MVP Budget

| Phase | Developer Days | ACU Cost |
|-------|---------------|----------|
| Phase 1: UX Improvements | 2.5 | 0 |
| Phase 2: Core AI Analysis | 3 | 30 |
| **Grand Total** | **5.5** | **30** |

## Implementation Details

### Phase 1: UX Improvements (Templates for Manual Implementation)

#### 1. Responsive Design Implementation (1 day, 0 ACU)
- **Template Provided**: CSS media query templates for key components
- **Manual Implementation**: Apply responsive CSS to Angular components
- **Gemini 2.0 Flash Usage**: Generate basic responsive CSS adjustments

#### 2. Historical Data Visualization (1 day, 0 ACU)
- **Template Provided**: Chart.js integration code sample
- **Manual Implementation**: Connect to Alpha Vantage historical data API
- **Gemini 2.0 Flash Usage**: Generate chart configuration options

#### 3. Save/Favorite Stocks Feature (0.5 day, 0 ACU)
- **Template Provided**: localStorage implementation code
- **Manual Implementation**: Add UI elements for favorites management
- **Gemini 2.0 Flash Usage**: Generate component HTML/CSS

### Phase 2: Core AI Analysis

#### 1. Improved Gemini Prompts Implementation (1 day, 5 ACU)
- Update GeminiAnalysisService with core prompts from PROMPTS.md
- Focus on company context and key metrics analysis only
- Implement structured JSON response handling

#### 2. Competitor Analysis Feature (1 day, 10 ACU)
- Create new endpoint in StockAnalysisController
- Implement competitor identification and comparison
- Develop simple UI component to display results

#### 3. Basic Trend Analysis (1 day, 15 ACU)
- Implement trend analysis for profitability and growth
- Create endpoint for basic trend insights
- Develop UI component with minimal styling

## Templates for Manual Implementation

To facilitate completion of UX features without using expensive AI models, the following templates will be provided:

### 1. Responsive Design Template
```css
/* Example media query template for stock-analyzer component */
@media (max-width: 768px) {
  .stock-analyzer-container {
    flex-direction: column;
  }
  
  .analysis-result {
    width: 100%;
    margin-top: 1rem;
  }
  
  /* Additional responsive adjustments */
}
```

### 2. Historical Data Chart Template
```typescript
// Example Chart.js integration
import { Chart } from 'chart.js/auto';

@Component({...})
export class StockHistoryComponent {
  @Input() symbol: string;
  chart: any;
  
  ngOnInit() {
    this.fetchHistoricalData();
  }
  
  fetchHistoricalData() {
    // TODO: Implement API call to fetch historical data
    // Example structure provided
  }
  
  createChart(data: any[]) {
    // TODO: Implement chart creation with provided data
    // Example configuration provided
  }
}
```

### 3. Favorites System Template
```typescript
// Example localStorage implementation
@Injectable({
  providedIn: 'root'
})
export class FavoritesService {
  private readonly STORAGE_KEY = 'stock-tok-favorites';
  
  getFavorites(): string[] {
    const stored = localStorage.getItem(this.STORAGE_KEY);
    return stored ? JSON.parse(stored) : [];
  }
  
  addFavorite(symbol: string): void {
    const favorites = this.getFavorites();
    if (!favorites.includes(symbol)) {
      favorites.push(symbol);
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(favorites));
    }
  }
  
  removeFavorite(symbol: string): void {
    const favorites = this.getFavorites();
    const index = favorites.indexOf(symbol);
    if (index !== -1) {
      favorites.splice(index, 1);
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(favorites));
    }
  }
}
```

## Cost Optimization Strategies

1. **Caching Responses**:
   - Cache Gemini API responses for common stocks
   - Implement TTL-based cache expiration (24 hours)
   - Estimated ACU savings: 30-40%

2. **Prompt Optimization**:
   - Use only essential prompts from PROMPTS.md
   - Limit response tokens by using structured formats
   - Estimated ACU savings: 20-25%

3. **Manual Implementation**:
   - Implement UX features manually using provided templates
   - Use Gemini 2.0 Flash for simple code generation tasks
   - Estimated ACU savings: 100% for UX features

## Features for Future Implementation

The following features should be considered for future iterations:

1. **Simple Predictive Indicators** (1 day, 15 ACU)
   - Basic moving averages and momentum indicators
   - Simple buy/sell signals based on technical analysis

2. **Portfolio Analysis** (2 days, 20 ACU)
   - Basic portfolio tracking functionality
   - Simple diversification analysis

3. **Dark Mode Toggle** (0.5 day, 0 ACU)
   - Theme switching functionality
   - Persistent theme preference

## Implementation Approach

### Two-Track Development Strategy

1. **AI-Powered Analysis (Gemini 2.5 Pro)**
   - Focus on core analytical features that require advanced AI capabilities
   - Implement only the highest-value features that justify the ACU cost
   - Ensure all prompts are optimized for token efficiency

2. **Template-Based UX Improvements (Manual/Gemini 2.0 Flash)**
   - Provide code templates for all UX features
   - Use Gemini 2.0 Flash (or manual coding) for implementation
   - Focus on features that provide immediate user value

### Notes

1. **Developer Days**: Estimates assume a skilled full-stack developer familiar with Spring Boot and Angular.

2. **ACU Costs**:
   - Only essential Gemini 2.5 Pro usage is budgeted
   - UX features use no ACUs by leveraging templates and Gemini 2.0 Flash
   - Implementing caching can further reduce the ACU cost by 30-40%

3. **Template Approach Benefits**:
   - Provides clear implementation guidance
   - Allows for manual completion without advanced AI
   - Ensures consistent code style and patterns

4. **Technical Constraints**:
   - All features designed to work within the existing Java + Angular architecture
   - No additional infrastructure requirements beyond what's already in place
   - All data storage uses client-side storage (localStorage) to avoid backend database changes

This revised budget provides a focused plan for implementing only the most critical AI-powered features within a 30 ACU budget while providing templates for UX improvements that can be implemented manually or with minimal AI assistance.
