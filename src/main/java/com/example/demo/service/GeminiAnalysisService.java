package com.example.demo.service;

import com.example.demo.dto.StockContextAnalysis;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode; // Import JsonNode for parsing
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.*; // Import needed classes like ResponseEntity, HttpStatus
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI; // Import URI
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Service
@PropertySource({"classpath:application.properties", "classpath:prompts.properties", "classpath:messages.properties"})
public class GeminiAnalysisService {

    @Value("${gemini.api.key}")
    private String geminiApiKeyConfig; // Use this field

    @Value("${gemini.project.id:default-project-id}")
    private String projectId;

    @Value("${gemini.location:us-central1}")
    private String location;

    // Using a more standard model name as default, adjust if needed
    @Value("${gemini.model.name:gemini-1.5-flash}")
    private String modelName;

    @Value("${gemini.prompt.context.json}")
    private String contextPromptJsonTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();
    // Consider injecting RestTemplate via constructor instead of creating directly
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private Environment env;

    public StockContextAnalysis analyzeStockContext(String ticker) throws IOException {
        // --- Input Validation ---
        if (ticker == null || ticker.trim().isEmpty()) {
            throw new IllegalArgumentException(Objects.requireNonNullElse(env.getProperty("error.ticker.empty"), "Ticker symbol cannot be empty."));
        }
        if (geminiApiKeyConfig == null || geminiApiKeyConfig.trim().isEmpty() || geminiApiKeyConfig.equals("YOUR_API_KEY")) { // Added placeholder check
            throw new IllegalArgumentException("Gemini API Key is not configured or is a placeholder. Set 'gemini.api.key' in application.properties.");
        }
        if (projectId == null || projectId.trim().isEmpty() || projectId.equals("default-project-id")) {
            System.err.println(String.format(Objects.requireNonNullElse(env.getProperty("log.gemini.warn.noProjectId"), "Warning: Gemini Project ID is not configured ('%s'). Check application.properties or environment variables."), projectId));
        }

        System.out.println("Using Gemini Model: " + modelName + " via REST API");

        // --- Prompt Creation ---
        String prompt = contextPromptJsonTemplate
                .replace("{COMPANY_NAME}", ticker) // Assuming company name = ticker for simplicity here
                .replace("{TICKER}", ticker);

        System.out.println(Objects.requireNonNullElse(env.getProperty("log.gemini.sendingPrompt"), "--- Sending Prompt ---"));
        System.out.println(prompt);
        System.out.println(Objects.requireNonNullElse(env.getProperty("log.gemini.endPrompt"), "--- End Prompt ---"));

        // === Start of REST API Call Section ===
        try {
            // --- 1. Construct the URL ---
            String baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent";
            URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                    // FIX 1: Use the injected configuration field geminiApiKeyConfig
                    .queryParam("key", this.geminiApiKeyConfig)
                    .build()
                    .toUri();

            // --- 2. Construct the Request Body ---
            Map<String, Object> textPart = Map.of("text", prompt);
            Map<String, Object> content = Map.of("parts", Collections.singletonList(textPart));
            Map<String, Object> requestBody = Map.of("contents", Collections.singletonList(content));

            // --- 3. Set Headers ---
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // --- 4. Create Request Entity ---
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // --- 5. Execute the POST Request ---
            ResponseEntity<String> response = restTemplate.postForEntity(uri, requestEntity, String.class);

            // --- FIX 2: Process Response - Check Status and Get Body ---
            String rawJsonResponseString; // Declare variable to hold the response body

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println(Objects.requireNonNullElse(env.getProperty("log.gemini.generated"), "Gemini response received successfully."));
                rawJsonResponseString = response.getBody(); // Assign the body
            } else {
                // Handle non-OK status codes explicitly if not caught by HttpClientErrorException
                String errorMsg = String.format("Gemini API request failed with status: %s, Body: %s",
                        response.getStatusCode(), response.getBody());
                System.err.println(errorMsg);
                throw new IOException(errorMsg);
            }
            // --- End Fix 2 ---


            // 6. Extract the text response from the raw JSON String
            if (rawJsonResponseString == null || rawJsonResponseString.trim().isEmpty()) {
                throw new IOException("Received empty response body from Gemini API.");
            }

            // Use rawJsonResponseString (which is now assigned)
            JsonNode rootNode = objectMapper.readTree(rawJsonResponseString);
            String extractedText = "";

            // Navigate through the JSON structure safely (Example assumes 'candidates' structure)
            if (rootNode.has("candidates") && rootNode.get("candidates").isArray() && !rootNode.get("candidates").isEmpty()) {
                JsonNode firstCandidate = rootNode.get("candidates").get(0);
                if (firstCandidate.has("content") && firstCandidate.get("content").has("parts") && firstCandidate.get("content").get("parts").isArray() && !firstCandidate.get("content").get("parts").isEmpty()) {
                    StringBuilder textBuilder = new StringBuilder();
                    for (JsonNode part : firstCandidate.get("content").get("parts")) {
                        if (part.has("text")) {
                            textBuilder.append(part.get("text").asText());
                        }
                    }
                    extractedText = textBuilder.toString();
                }
            }

            // Check if text was actually extracted
            if (extractedText.isEmpty()) {
                 // Check for safety ratings blocking if no text found
                 if (rootNode.has("promptFeedback") && rootNode.get("promptFeedback").has("blockReason")) {
                    String blockReason = rootNode.get("promptFeedback").get("blockReason").asText();
                    String safetyRatings = rootNode.get("promptFeedback").has("safetyRatings") ? rootNode.get("promptFeedback").get("safetyRatings").toString() : "N/A";
                     String errorMsg = String.format(Objects.requireNonNullElse(env.getProperty("error.gemini.blocked"), "Gemini response blocked. Reason: %s. Safety Ratings: %s"), blockReason, safetyRatings);
                     System.err.println(errorMsg);
                     System.err.println("Raw Response: " + rawJsonResponseString);
                     throw new IOException(errorMsg); // Throw specific error for blocked content
                 } else {
                    // If not blocked, but still no text extracted
                    System.err.println("Warning: Could not extract text content from Gemini response structure.");
                    System.err.println("Raw Response: " + rawJsonResponseString);
                    // Depending on requirements, either throw an error or try to proceed
                    // Throwing error is safer if the structure is unexpected
                     throw new IOException("Could not extract text content from Gemini response structure. Raw: " + rawJsonResponseString);
                 }
            }


            // Keep original logging for raw response (now using the extracted text)
            System.out.println(Objects.requireNonNullElse(env.getProperty("log.gemini.rawResponse"), "--- Raw Gemini Response (Extracted Text) ---"));
            System.out.println(extractedText); // Log the extracted text
            System.out.println(Objects.requireNonNullElse(env.getProperty("log.gemini.endRawResponse"), "--- End Raw Gemini Response ---"));

            // Keep original cleaning logic
            String cleanedJson = cleanGeminiJsonResponse(extractedText); // Clean the extracted text

            // Handle cases where cleaning might result in null/empty if extraction failed
            if (cleanedJson == null || cleanedJson.trim().isEmpty()) {
                throw new IOException("Failed to extract or clean valid JSON content from Gemini response. Was: " + extractedText);
            }

            // Keep original parsing logic
            StockContextAnalysis analysis = objectMapper.readValue(cleanedJson, StockContextAnalysis.class);
            System.out.println(Objects.requireNonNullElse(env.getProperty("log.gemini.parsingSuccessful"), "Gemini response parsed successfully."));
            return analysis;

        // Keep original catch blocks, maybe add RestClientException
        } catch (JsonProcessingException e) { // Catch JSON parsing errors (both request building and response parsing)
            String errorMsg = String.format(Objects.requireNonNullElse(env.getProperty("error.gemini.parsing"), "ERROR processing JSON for Gemini: %s"), e.getMessage());
            System.err.println(errorMsg);
            // Log the string that failed to parse if possible and not too large
            // Be cautious logging potentially sensitive data from responses here
            e.printStackTrace();
            throw new IOException(String.format(Objects.requireNonNullElse(env.getProperty("error.gemini.failedParse"), "Failed to process JSON for Gemini analysis: %s"), e.getMessage()), e);
        } catch (HttpClientErrorException e) { // Specifically catch HTTP errors from RestTemplate
            String responseBody = e.getResponseBodyAsString();
            String errorMsg = String.format(Objects.requireNonNullElse(env.getProperty("error.gemini.apiHttpError"), "ERROR calling Gemini API (HTTP %s): %s"),
                                           e.getStatusCode(), responseBody);
            System.err.println(errorMsg);
            // Attempt to parse error details from Google's standard error format
             try {
                JsonNode errorNode = objectMapper.readTree(responseBody);
                 if(errorNode.has("error") && errorNode.get("error").has("message")) {
                    System.err.println("Google API Error Message: " + errorNode.get("error").get("message").asText());
                 }
             } catch (Exception parseEx) {
                 System.err.println("Could not parse error response body.");
             }
            e.printStackTrace();
            throw new IOException(String.format(Objects.requireNonNullElse(env.getProperty("error.gemini.failedAnalysisHttp"), "Failed to get analysis from Gemini (HTTP Error %s): %s"), e.getStatusCode(), e.getMessage()), e);
        } catch (RestClientException e) { // Catch other RestTemplate errors (network, I/O, etc.)
            String errorMsg = String.format(Objects.requireNonNullElse(env.getProperty("error.gemini.apiRestError"), "ERROR during Gemini REST call: %s"), e.getMessage());
            System.err.println(errorMsg);
            e.printStackTrace();
            throw new IOException(String.format(Objects.requireNonNullElse(env.getProperty("error.gemini.failedAnalysisRest"), "Failed to get analysis from Gemini (REST Client Error): %s"), e.getMessage()), e);
        } catch (IOException e) { // Catch IOExceptions thrown explicitly within the try block
             System.err.println("IOException during Gemini processing: " + e.getMessage());
             // Don't print stack trace again if it's one we threw explicitly
             if (!(e.getCause() instanceof HttpClientErrorException || e.getCause() instanceof RestClientException)) {
                 e.printStackTrace();
             }
             throw e; // Re-throw the original IOException
         } catch (Exception e) { // General catch block remains for unexpected errors
            String errorMsg = String.format(Objects.requireNonNullElse(env.getProperty("error.gemini.apiOrProcessing"), "UNEXPECTED ERROR during Gemini processing: %s"), e.getMessage());
            System.err.println(errorMsg);
            e.printStackTrace();
            throw new IOException(String.format(Objects.requireNonNullElse(env.getProperty("error.gemini.failedAnalysis"), "Failed to get analysis from Gemini: %s"), e.getMessage()), e);
        }
        // === End of REST API Call Section ===
    }

    // Keep the cleanGeminiJsonResponse method exactly as it was
    private String cleanGeminiJsonResponse(String rawResponse) {
         if (rawResponse == null) return null;
         String cleaned = rawResponse.trim();
         // Remove potential markdown code block fences
         if (cleaned.startsWith("```json")) {
             cleaned = cleaned.substring(7); // Length of "```json\n" or similar
             if (cleaned.endsWith("```")) {
                 cleaned = cleaned.substring(0, cleaned.length() - 3);
             }
         } else if (cleaned.startsWith("```")) {
             cleaned = cleaned.substring(3);
             if (cleaned.endsWith("```")) {
                 cleaned = cleaned.substring(0, cleaned.length() - 3);
             }
         }
         // Trim again after removing fences
         cleaned = cleaned.trim();

         // Find the first '{' and the last '}'
         int firstBrace = cleaned.indexOf('{');
         int lastBrace = cleaned.lastIndexOf('}');

         // Ensure both braces are found and in the correct order
         if (firstBrace != -1 && lastBrace != -1 && lastBrace >= firstBrace) {
             // Extract the content between the first '{' and the last '}'
             return cleaned.substring(firstBrace, lastBrace + 1);
         } else {
             // Log a warning if the expected JSON delimiters aren't found
             System.err.println(Objects.requireNonNullElse(env.getProperty("log.gemini.warn.jsonDelimiters"),
                     "Warning: Could not find matching '{' and '}' in the cleaned Gemini response. Returning cleaned response as-is."));
             System.err.println("Cleaned response was: " + cleaned);
             // Return the cleaned string; subsequent JSON parsing will likely fail, which is intended behavior.
             return cleaned;
         }
     }
}