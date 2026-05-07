package com.vectorbridge.resource;

import org.slf4j.LoggerFactory;

import com.vectorbridge.db.DatabaseManager;
import com.vectorbridge.model.Customer;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

@Path("/customers")
public class CustomerResource {
    private static final Logger log = LoggerFactory.getLogger(CustomerResource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomers(){
        List<Customer> customers = new ArrayList<>();
        try(Connection conn = DatabaseManager.getConnection()){
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM customers ORDER BY id");

            while(rs.next()){
                customers.add(new Customer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("city"),
                    rs.getString("signup_date")));

            }
            log.info("Fetched {} customers", customers.size());
            return Response.ok(customers).build();
        }catch(SQLException e){
            log.error("Error fetching customers", e);
            return Response.serverError()
                .entity("{\"error\":\"Failed to fetch customers\"}")
                .build();


    }
}
}
