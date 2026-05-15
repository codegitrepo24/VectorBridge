package com.vectorbridge.resource;

import com.vectorbridge.ai.GeminiClient;
import com.vectorbridge.ai.SearchResult;
import com.vectorbridge.ai.SemanticSearchService;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("/vectors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VectorResource {

    // Helper class to map incoming JSON requests
    public static class TextRequest {
        public String text;
    }

    @POST
    @Path("/embed-test")
    public Response testEmbedding(TextRequest request) {
        if (request == null || request.text == null || request.text.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Text field is required"))
                    .build();
        }

        try {
            // 1. Create an instance of your client
            GeminiClient aiClient = new GeminiClient();

            // 2. Call the method inside a try block
            String embeddingJsonArray = aiClient.generateEmbedding(request.text);

            // 3. Return the array
            return Response.ok(embeddingJsonArray).build();

        } catch (Exception e) {
            // 4. Catch the exception and return a clean 500 Server Error to the user
            e.printStackTrace();
            return Response.serverError()
                    .entity(Map.of("error", "Failed to contact Gemini API: " + e.getMessage()))
                    .build();
        }
    }

    // Wrapper class for incoming search requests
    public static class SearchRequest {
        private String query;
        private int limit = 5; // Default to top 5 results if not provided

        public SearchRequest() {
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }
    }

    @POST
    @Path("/search")
    public Response semanticSearch(SearchRequest request) {
        if (request == null || request.getQuery() == null || request.getQuery().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Search query is required"))
                    .build();
        }

        try {
            SemanticSearchService searchService = new SemanticSearchService();
            List<SearchResult> topResults = searchService.search(request.getQuery(), request.getLimit());

            return Response.ok(topResults).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity(Map.of("error", "Search failed: " + e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/index-tickets")
    public Response indexTickets() {
        try {
            SemanticSearchService searchService = new SemanticSearchService();
            int count = searchService.indexSupportTickets();
            
            return Response.ok(Map.of(
                "message", "Successfully embedded and indexed " + count + " support tickets."
            )).build();
            
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity(Map.of("error", "Indexing failed: " + e.getMessage()))
                    .build();
        }
    }
}