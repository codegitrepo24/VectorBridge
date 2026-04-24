package com.vectorbridge.server;

import com.vectorbridge.resource.HealthResource;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.eclipse.jetty.server.Server;

import java.net.URI;

public class JettyServer {

    private final Server server;

    public JettyServer(int port) throws Exception {
        ResourceConfig config = new ResourceConfig();
        config.registerClasses(HealthResource.class);
        config.register(JacksonFeature.class);

        server = JettyHttpContainerFactory.createServer(
            URI.create("http://0.0.0.0:" + port + "/"),
            config,
            false
        );
    }

    public void start() throws Exception {
        server.start();
    }

    public void join() throws InterruptedException {
        server.join();
    }
}