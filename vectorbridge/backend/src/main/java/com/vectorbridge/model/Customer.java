package com.vectorbridge.model;

public class Customer {
  private int id;
  private String name;
  private String email;
  private String city;
  private String signupDate;
  
    public Customer(int id, String name, String email, String city, String signupDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.city = city;
        this.signupDate = signupDate;
    }

    // Getters — Jackson needs these to serialize to JSON
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCity() {
        return city;
    }

    public String getSignupDate() {
        return signupDate;
    }
}
