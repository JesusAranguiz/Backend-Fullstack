package com.levelup.backend.model;

import jakarta.persistence.*; // o javax.persistence.* según versión

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // usa AUTO_INCREMENT en MySQL
    private Long id;
    @Column(nullable = false, length = 120)
    private String name;
    private String image;
    private Double price;
    private String description;
    private Boolean oferta;
    private String category;
    private Integer stock;
    

    // Constructor vacío obligatorio para JPA
    public Product() {
    }

    // Constructor con todos los parámetros
    public Product(Long id, String name, String image, Double price, String description, Boolean oferta, String category, Integer stock) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;
        this.description = description;
        this.oferta = oferta;
        this.category = category;
        this.stock = stock;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getOferta() {
        return oferta;
    }

    public void setOferta(Boolean oferta) {
        this.oferta = oferta;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
