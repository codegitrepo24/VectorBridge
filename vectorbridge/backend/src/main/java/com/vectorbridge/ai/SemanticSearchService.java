package com.vectorbridge.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vectorbridge.db.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SemanticSearchService {
    private static final Logger log = LoggerFactory.getLogger(SemanticSearchService.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    public List<SearchResult> search(String query, int limit) throws Exception {
        // 1. Convert the user's text search into a vector using Gemini
        log.info("Generating embedding for search query: '{}'", query);
        GeminiClient aiClient = new GeminiClient();
        String queryVectorJson = aiClient.generateEmbedding(query);

        // Parse the JSON array string back into a Java double array
        double[] queryVector = mapper.readValue(queryVectorJson, double[].class);

        List<SearchResult> allResults = new ArrayList<>();

        // 2. Fetch all stored vectors from the database
        String sql = "SELECT source_table, source_id, text_content, vector FROM embeddings";

        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String sourceTable = rs.getString("source_table");
                int sourceId = rs.getInt("source_id");
                String textContent = rs.getString("text_content");
                String dbVectorJson = rs.getString("vector");

                // 3. Convert DB JSON vector string to a double array
                double[] dbVector = mapper.readValue(dbVectorJson, double[].class);

                // 4. Calculate similarity using your VectorMath class!
                double score = VectorMath.cosineSimilarity(queryVector, dbVector);

                // 5. Save the result
                allResults.add(new SearchResult(sourceId, sourceTable, textContent, score));
            }
        }

        // 6. Sort the results (Highest score first, thanks to our SearchResult
        // compareTo method)
        Collections.sort(allResults);

        // 7. Return only the Top K results (limit)
        if (allResults.size() > limit) {
            return allResults.subList(0, limit);
        }
        return allResults;
    }

    public int indexSupportTickets() throws Exception {
        log.info("Starting to index support tickets...");
        GeminiClient aiClient = new GeminiClient();
        int count = 0;

        // 1. Clear existing ticket vectors so we don't create duplicates if run twice
        String deleteSql = "DELETE FROM embeddings WHERE source_table = 'support_tickets'";
        // 2. Fetch the tickets
        String selectSql = "SELECT id, description FROM support_tickets";
        // 3. Insert the vectors
        String insertSql = "INSERT INTO embeddings (source_table, source_id, text_content, vector) VALUES ('support_tickets', ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
             PreparedStatement selectStmt = conn.prepareStatement(selectSql);
             ResultSet rs = selectStmt.executeQuery();
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            // Execute the cleanup first
            deleteStmt.executeUpdate();

            // Loop through every ticket in the database
            while (rs.next()) {
                int ticketId = rs.getInt("id");
                String description = rs.getString("description");

                log.info("Generating vector for Ticket ID {}: {}", ticketId, description);
                
                // Call Gemini to get the vector for this ticket's description
                String vectorJson = aiClient.generateEmbedding(description);

                if (vectorJson != null) {
                    // Save the vector into the embeddings table
                    insertStmt.setInt(1, ticketId);
                    insertStmt.setString(2, description);
                    insertStmt.setString(3, vectorJson);
                    insertStmt.executeUpdate();
                    count++;
                }
            }
        }
        
        log.info("Successfully indexed {} support tickets.", count);
        return count;
    }

}
