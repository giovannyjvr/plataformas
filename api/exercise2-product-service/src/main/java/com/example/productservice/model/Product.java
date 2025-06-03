package com.insper.productservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class Product {

    private UUID id;
    private String name;
    private double price;
    private String unit;

    // Construtor vazio (para Jackson)
    public Product() {
    }

    // Construtor “full” (utilizado internamente ao criar)
    public Product(UUID id, String name, double price, String unit) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.unit = unit;
    }

    // Getters e Setters

    @JsonProperty("id")
    public UUID getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(UUID id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("price")
    public double getPrice() {
        return price;
    }
    @JsonProperty("price")
    public void setPrice(double price) {
        this.price = price;
    }

    @JsonProperty("unit")
    public String getUnit() {
        return unit;
    }
    @JsonProperty("unit")
    public void setUnit(String unit) {
        this.unit = unit;
    }
}
