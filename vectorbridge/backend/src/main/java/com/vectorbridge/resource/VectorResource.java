package com.vectorbridge.resource;

import com.vectorbridge.ai.GeminiClient;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
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
}