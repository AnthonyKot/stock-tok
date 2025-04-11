import { Component } from '@angular/core';
import { StockAnalyzerComponent } from './stock-analyzer/stock-analyzer.component';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    standalone: true,
    imports: [StockAnalyzerComponent],
    styleUrl: './app.component.css'
})
export class AppComponent {
    title = 'stock-ui';
}
