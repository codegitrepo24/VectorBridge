package com.vectorbridge.resource;

import com.vectorbridge.ai.GeminiClient;
// import com.vectorbridge.ai.QueryResult; // Why: This class is no longer used in the current implementation, but we keep it here for potential future use when we want to return a more structured response that includes both the generated SQL and the execution results/errors in one object.
import com.vectorbridge.db.DatabaseManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

@Path("/api/ai")
// Moving these to the class level applies them to all endpoints automatically
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AiQueryResource {

    private static final Logger log = LoggerFactory.getLogger(AiQueryResource.class);
    private final GeminiClient geminiClient = new GeminiClient();

    /**
     * Step 1: Translate Natural Language to SQL
     */
    @POST
    @Path("/generate-sql")
    public Response generateSql(Map<String, String> body) {
        String userQuery = body.get("query");

        if (userQuery == null || userQuery.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", "Query cannot be empty"))
                .build();
        }

        log.info("Generating SQL for AI query: {}", userQuery);

        try {
            String generatedSql = geminiClient.generateSql(userQuery);
            return Response.ok(Map.of("sql", generatedSql)).build();
        } catch (Exception e) {
            log.error("AI SQL generation failed", e);
            return Response.serverError()
                .entity(Map.of("error", "Failed: " + e.getMessage()))
                .build();
        }
    }

    /**
     * Step 2: Execute the generated/edited SQL against the database
     */
    @POST
    @Path("/execute-sql")
    public Response executeSql(Map<String, String> body) {
        String sql = body.get("sql");

        if (sql == null || sql.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", "SQL cannot be empty"))
                .build();
        }

        // SECURITY CHECK: Prevent destructive operations
        String upperSql = sql.toUpperCase();
        if (upperSql.contains("DROP") || upperSql.contains("DELETE") || upperSql.contains("UPDATE") || upperSql.contains("INSERT")) {
            log.warn("Blocked destructive SQL query: {}", sql);
            return Response.status(Response.Status.FORBIDDEN)
                .entity(Map.of("error", "Only SELECT queries are allowed for safety."))
                .build();
        }

        log.info("Executing SQL: {}", sql);

        try {
            List<Map<String, Object>> results = executeQuery(sql);
            return Response.ok(results).build();
        } catch (Exception e) {
            log.error("SQL Execution failed", e);
            return Response.serverError()
                .entity(Map.of("error", "Database execution failed: " + e.getMessage()))
                .build();
        }
    }

    /**
     * Your original, perfect helper method.
     */
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