import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http'; // Import HttpClient
import { Observable } from 'rxjs'; // Import Observable

@Injectable({
  providedIn: 'root'
})
export class StockApiService {

  // Base URL of your Spring Boot backend (updated to port 3000)
  private apiUrl = 'http://localhost:8080/api/analyze'; 

  // Inject HttpClient
  constructor(private http: HttpClient) { }

  // Method to get analysis data
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
}
