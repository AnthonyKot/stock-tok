import { Component } from '@angular/core';
import { StockApiService } from '../services/stock-api.service'; // Import the service
import { CommonModule } from '@angular/common'; // Import CommonModule for *ngIf, *ngFor etc.
import { FormsModule } from '@angular/forms'; // Import FormsModule for ngModel

@Component({
  selector: 'app-stock-analyzer',
  standalone: true, // Mark as standalone
  imports: [CommonModule, FormsModule], // Import necessary modules
  templateUrl: './stock-analyzer.component.html',
  styleUrls: ['./stock-analyzer.component.css']
})
export class StockAnalyzerComponent {
  ticker: string = ''; // Input field model
  analysisData: any = null; // To store the response
  isLoading: boolean = false;
  errorMessage: string | null = null;

  // Inject the service
  constructor(private stockApi: StockApiService) { }

  analyzeStock() {
    if (!this.ticker) {
      this.errorMessage = 'Please enter a stock ticker.';
      this.analysisData = null;
      return;
    }
    console.log(`Analyzing stock: ${this.ticker}`);
    this.isLoading = true;
    this.errorMessage = null;
    this.analysisData = null; // Clear previous results

    this.stockApi.getAnalysis(this.ticker).subscribe({
      next: (data) => {
        console.log('Received data:', data);
        this.analysisData = data;
        // Try parsing the quoteData string into an object for easier access
        if (this.analysisData && typeof this.analysisData.quoteData === 'string') {
          try {
            this.analysisData.quoteData = JSON.parse(this.analysisData.quoteData);
          } catch (e) {
            console.error('Could not parse quoteData JSON:', e);
            // Keep raw string if parsing fails
          }
        }
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching analysis:', err);
        this.errorMessage = err.error?.message || err.message || 'Failed to fetch analysis data.';
        this.isLoading = false;
      }
    });
  }

  // Helper to get quote details safely
  getQuoteValue(key: string): string {
    return this.analysisData?.quoteData?.['Global Quote']?.[key] ?? 'N/A';
  }
}
