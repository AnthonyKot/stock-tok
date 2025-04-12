import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http'; // Import HttpClient
import { Observable } from 'rxjs'; // Import Observable

/**
 * Service for interacting with the stock analysis API.
 * Implementation Complexity: 5/10
 * 
 * This service requires:
 * - HTTP client integration
 * - Error handling
 * - Multiple endpoint methods
 * - Response type mapping
 */
@Injectable({
  providedIn: 'root'
})
export class StockApiService {

  private apiUrl = 'http://localhost:8080/api/analyze'; 

  // Inject HttpClient
  constructor(private http: HttpClient) { }

  /**
   * Gets basic stock analysis.
   * 
   * @param symbol Stock ticker symbol
   * @return Observable with stock analysis data
   */
  getAnalysis(symbol: string): Observable<any> { // Use 'any' for now, create interfaces later for better typing
    if (!symbol) {
      return new Observable(observer => {
        observer.error('Stock symbol cannot be empty');
        observer.complete();
      });
    }
    const url = `${this.apiUrl}/${symbol.toUpperCase()}`;
    console.log('Requesting analysis from:', url);
    return this.http.get<any>(url);
  }
  
  /**
   * Gets historical stock data.
   * 
   * @param symbol Stock ticker symbol
   * @param timeframe Data timeframe (compact or full)
   * @return Observable with historical data
   */
  getHistoricalData(symbol: string, timeframe: string = 'compact'): Observable<any> {
    if (!symbol) {
      return new Observable(observer => {
        observer.error('Stock symbol cannot be empty');
        observer.complete();
      });
    }
    
    const url = `${this.apiUrl}/historical/${symbol.toUpperCase()}?timeframe=${timeframe}`;
    console.log('Requesting historical data from:', url);
    return this.http.get<any>(url);
  }
  
  /**
   * Gets competitor analysis.
   * 
   * @param symbol Stock ticker symbol
   * @return Observable with competitor analysis data
   */
  getCompetitorAnalysis(symbol: string): Observable<any> {
    if (!symbol) {
      return new Observable(observer => {
        observer.error('Stock symbol cannot be empty');
        observer.complete();
      });
    }
    
    const url = `${this.apiUrl}/competitors/${symbol.toUpperCase()}`;
    console.log('Requesting competitor analysis from:', url);
    return this.http.get<any>(url);
  }
  
  /**
   * Gets trend analysis.
   * 
   * @param symbol Stock ticker symbol
   * @return Observable with trend analysis data
   */
  getTrendAnalysis(symbol: string): Observable<any> {
    if (!symbol) {
      return new Observable(observer => {
        observer.error('Stock symbol cannot be empty');
        observer.complete();
      });
    }
    
    const url = `${this.apiUrl}/trends/${symbol.toUpperCase()}`;
    console.log('Requesting trend analysis from:', url);
    return this.http.get<any>(url);
  }
}
