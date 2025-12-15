package com.camaras.seguridadcamarasweb.model;

import java.math.BigDecimal;

import com.camaras.seguridadcamarasweb.enums.ProductType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    
    private String description;

    private String brand; // Campo necesario para el filtro de marca

    @Enumerated(EnumType.STRING) // Almacena el nombre del enum (IP o ANALOGICA)
    private ProductType type; 

    @Column(nullable = false)
    private BigDecimal unitPrice;
    
    private Integer stock;

    // --- Constructor vacío ---
    public Product() {}
    
    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public ProductType getType() { return type; }
    public void setType(ProductType type) { this.type = type; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    
}
