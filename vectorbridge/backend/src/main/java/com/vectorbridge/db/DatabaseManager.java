package com.vectorbridge.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    private static final Logger log = LoggerFactory.getLogger(DatabaseManager.class);
    private static HikariDataSource dataSource;

    public static void initialize(){
        HikariConfig config = new HikariConfig();

        // H2 file-based DB — persists between restarts
        config.setJdbcUrl("jdbc:h2:./data/vectorbridgr;AUTO_SERVER=TRUE");
        config.setUsername("sa");
        config.setPassword("");
        config.setDriverClassName("org.h2.Driver");

        // Pool settings
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(60000);
        config.setMaxLifetime(1800000);
        config.setPoolName("VectorBridgePool");

        dataSource = new HikariDataSource(config);
        log.info("Database connection pool initialized.");

    }

    public static Connection getConnection() throws SQLException{
        return dataSource.getConnection();
    }

    public static void shutdown(){
        if(dataSource != null && !dataSource.isClosed()){
            dataSource.close();
            log.info("Database connection pool shut down.");
        }
    }

}
