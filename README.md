# Stock-Tok: AI-Enhanced Stock Analysis

Stock-Tok is a full-stack web application that provides AI-enhanced stock analysis. Users can enter a stock symbol and receive comprehensive analysis including raw stock quote data from Alpha Vantage and AI-generated contextual information about the stock using Google's Gemini AI.

## Features

- **Stock Quote Data**: Fetches real-time stock information from Alpha Vantage API
- **AI Contextual Analysis**: Uses Google's Gemini AI to analyze various factors about stocks
- **User-Friendly Interface**: Simple Angular frontend for entering stock symbols and viewing analysis
- **Comprehensive Results**: Displays both raw data and AI-generated insights in an easy-to-read format

## Prerequisites

- Java 17 or higher
- Node.js 18.x and npm
- Maven 3.9+
- Google Cloud account with Gemini API access
- Alpha Vantage API key

## Setup & Installation

### Configuration

1. Clone the repository:
   ```sh
   git clone https://github.com/AnthonyKot/stock-tok.git
   cd stock-tok
   ```

2. Configure API keys in `src/main/resources/application.properties`:
   ```properties
   alpha.vantage.api.key=YOUR_ALPHA_VANTAGE_API_KEY
   gemini.api.key=YOUR_GEMINI_API_KEY
   ```

### Backend Setup

1. Build and run the Spring Boot backend:
   ```sh
   mvn spring-boot:run
   ```
   The backend will start on http://localhost:8080

### Frontend Setup (Development Mode)

1. Navigate to the Angular project directory:
   ```sh
   cd stock-ui
   ```

2. Install dependencies:
   ```sh
   npm install
   ```

3. Start the Angular development server:
   ```sh
   ng serve
   ```
   The frontend will be available at http://localhost:4200

### Building for Production

The project includes a multi-stage Dockerfile that builds both the frontend and backend:

```sh
docker build -t stock-tok .
docker run -p 8080:8080 stock-tok
```

Access the application at http://localhost:8080

## Architecture

- **Spring Boot Backend**: Handles API requests, coordinates data retrieval and AI analysis
- **Angular Frontend**: Provides user interface for stock analysis
- **External Services**:
  - Google Gemini AI for contextual stock analysis
  - Alpha Vantage API for stock quote data

## API Endpoints

- `GET /api/analyze/{symbol}`: Analyzes the specified stock symbol and returns combined data

## License

This project is licensed under the MIT License - see the LICENSE file for details.
