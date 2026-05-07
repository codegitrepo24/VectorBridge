package com.vectorbridge.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Statement;


public class DataLoader {
    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    public static void load(){
        try(Connection conn = DatabaseManager.getConnection()){
            Statement stmt = conn.createStatement();

            // Create tables
            stmt.execute("CREATE TABLE IF NOT EXISTS customers (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT," +
                        "name VARCHAR(255) NOT NULL," +
                        "email VARCHAR(255) UNIQUE NOT NULL," +
                        "city VARCHAR(255)," +
                        "signup_date DATE" +
                    ")");

              stmt.execute("CREATE TABLE IF NOT EXISTS orders (" +
                " id          INT PRIMARY KEY AUTO_INCREMENT," +
                " customer_id INT NOT NULL," +
                " product     VARCHAR(100)," +
                " amount      DECIMAL(10,2)," +
                " status      VARCHAR(20)," +
                " created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                " FOREIGN KEY (customer_id) REFERENCES customers(id)" +
            ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS support_tickets(" +
                    " id INT PRIMARY KEY AUTO_INCREMENT," +
                    " customer_id INT NOT NULL," +
                    " description TEXT," +
                    " priority VARCHAR(10)," +
                    " resolved BOOLEAN DEFAULT FALSE," +
                    " FOREIGN KEY (customer_id) REFERENCES customers(id)" +
                    ")");

            // Insert sample data only if empty
            stmt.execute("MERGE INTO customers (id, name, email, city, signup_date) KEY(id) VALUES" +
                        " (1, 'Kundan Chaudhari', 'kundan@example.com', 'Pune', '2023-01-15')," +
                    " (2, 'Priya Sharma',     'priya@example.com',  'Mumbai', '2023-03-22')," +
                    " (3, 'Rahul Verma',      'rahul@example.com',  'Delhi', '2023-05-10')," +
                    " (4, 'Anita Desai',      'anita@example.com',  'Bangalore', '2023-07-01')," +
                    " (5, 'Vikram Singh',     'vikram@example.com', 'Hyderabad', '2024-01-20')," +
                    " (6, 'Sneha Patil',      'sneha@example.com',  'Pune', '2024-02-14')," +
                    " (7, 'Amit Kumar',       'amit@example.com',   'Chennai', '2024-03-30')," +
                    " (8, 'Neha Joshi',       'neha@example.com',   'Kolkata', '2024-06-05')");

                    stmt.execute("MERGE INTO orders (id, customer_id, product, amount, status) KEY(id) VALUES" +
            " (1, 1, 'Database Pro License', 4999.00, 'completed')," +
            " (2, 2, 'AI Analytics Suite',  9999.00, 'pending')," +
            " (3, 3, 'Cloud Storage Plan',  1999.00, 'completed')," +
            " (4, 4, 'Security Module',     3499.00, 'failed')," +
            " (5, 1, 'Support Package',     1499.00, 'completed')," +
            " (6, 5, 'Database Pro License',4999.00, 'pending')," +
            " (7, 6, 'AI Analytics Suite',  9999.00, 'completed')," +
            " (8, 7, 'Cloud Storage Plan',  1999.00, 'completed')");

         stmt.execute("MERGE INTO support_tickets (id, customer_id, description, priority, resolved) KEY(id) VALUES" +
                " (1, 1, 'Cannot connect to database after update', 'high', false)," +
                " (2, 2, 'AI query returning wrong results', 'high', false)," +
                " (3, 3, 'Slow query performance on large datasets', 'medium', true)," +
                " (4, 4, 'Need help with schema migration', 'low', false)," +
                " (5, 5, 'Dashboard not loading charts', 'medium', false)," +
                " (6, 6, 'Export feature broken for CSV files', 'low', true)," +
                " (7, 7, 'Login fails after password reset', 'high', false)," +
                " (8, 8, 'Email notifications not working', 'medium', true)");
            
            log.info("Sample data loaded successfully.");

        } catch (Exception e) {
            log.error("Failed to load sample data", e);
        }

    }

}
