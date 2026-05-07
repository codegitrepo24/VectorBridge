package com.vectorbridge.resource;

import com.vectorbridge.ai.GeminiClient;
import com.vectorbridge.ai.QueryResult;
import com.vectorbridge.db.DatabaseManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

@Path("/ai")
public class AiQueryResource {

    private static final Logger log = LoggerFactory.getLogger(AiQueryResource.class);
    private final GeminiClient geminiClient = new GeminiClient();

    @POST
    @Path("/query")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response query(Map<String, String> body) {

        String userQuery = body.get("query");

        if (userQuery == null || userQuery.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new QueryResult(userQuery, "Query cannot be empty"))
                .build();
        }

        log.info("AI query received: {}", userQuery);

        try {
            // Step 1: Ask Gemini to generate SQL
            String generatedSql = geminiClient.generateSql(userQuery);

            // Step 2: Run the generated SQL against H2
            List<Map<String, Object>> results = executeQuery(generatedSql);

            // Step 3: Return results
            QueryResult result = new QueryResult(userQuery, generatedSql, results);
            return Response.ok(result).build();

        } catch (Exception e) {
            log.error("AI query failed", e);
            return Response.serverError()
                .entity(new QueryResult(userQuery, "Failed: " + e.getMessage()))
                .build();
        }
    }

    private List<Map<String, Object>> executeQuery(String sql) throws Exception {
        List<Map<String, Object>> rows = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(
                        meta.getColumnName(i).toLowerCase(),
                        rs.getObject(i)
                    );
                }
                rows.add(row);
            }
        }

        return rows;
    }
}