package com.vectorbridge.resource;   // ✅ correct package

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.Instant;
import java.util.Map;

@Path("/health")
public class HealthResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response health() {
        Map<String, Object> status = Map.of(
            "status", "UP",
            "app", "VectorBridge",
            "version", "1.0.0",
            "timestamp", Instant.now().toString()
        );
        return Response.ok(status).build();
    }
}