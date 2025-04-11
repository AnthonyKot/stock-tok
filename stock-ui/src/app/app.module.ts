import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http'; // Import HttpClientModule
import { FormsModule } from '@angular/forms'; // Import FormsModule for ngModel

import { AppComponent } from './app.component';
// StockAnalyzerComponent is now standalone, no need to declare here
// import { StockAnalyzerComponent } from './stock-analyzer/stock-analyzer.component'; 

@NgModule({
  declarations: [],
  imports: [
    BrowserModule,
    HttpClientModule, // Add HttpClientModule here
    FormsModule // Add FormsModule here for template-driven forms (like ngModel)
    // StockAnalyzerComponent is imported directly where used (in AppComponent template)
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
