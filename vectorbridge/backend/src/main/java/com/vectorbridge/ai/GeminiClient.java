package com.vectorbridge.ai;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeminiClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiClient.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String API_KEY = System.getenv("GEMINI_API_KEY");

    private static final String API_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=" + API_KEY;


    private static final String SCHEMA_CONTEXT = """
        You are a SQL expert. Generate SQL queries for an H2 database with these tables:
        
        customers (id INT, name VARCHAR, email VARCHAR, city VARCHAR, signup_date DATE)
        
        orders (id INT, customer_id INT, product VARCHAR, amount DECIMAL,
                status VARCHAR -- values: 'completed', 'pending', 'failed',
                created_at TIMESTAMP)
        
        support_tickets (id INT, customer_id INT, description TEXT,
                         priority VARCHAR -- values: 'high', 'medium', 'low',
                         resolved BOOLEAN)
        
        Rules:
        - Return ONLY the SQL query, nothing else
        - No explanations, no markdown, no code blocks
        - Use standard H2-compatible SQL only
        - Always use table aliases for clarity
        - For JOINs between orders/tickets and customers, use customer_id
        """;

    public String generateSql(String naturalLanguageQuery) throws Exception {
        // Build request body
        String requestBody = mapper.writeValueAsString(Map.of(
            "contents", List.of(Map.of(
                "parts", List.of(Map.of(
                    "text", SCHEMA_CONTEXT + "\n\nUser question: " + naturalLanguageQuery
                ))
            ))
        ));

        // Call Gemini API
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(API_URL);
            request.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
            request.setHeader("Content-Type", "application/json");

            return httpClient.execute(request, response -> {
                String responseBody = new String(response.getEntity().getContent().readAllBytes());
                log.debug("Gemini raw response: {}", responseBody);


                JsonNode root = mapper.readTree(responseBody);

                // Extract generated text from Gemini response
                String sql = root
                    .path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text")
                    .asText()
                    .trim();

                // Clean up in case Gemini adds markdown anyway
                sql = sql.replace("```sql", "").replace("```", "").trim();

                log.info("Generated SQL: {}", sql);
                return sql;
            });
        }
    }
}