package com.vectorbridge;

import com.vectorbridge.server.JettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App{
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception{
        log.info("Starting VectorBridge....");
        JettyServer server = new JettyServer(8080);
        server.start();
        log.info("VectorBridge running at http://localhost:8080");
        server.join();
    }
}