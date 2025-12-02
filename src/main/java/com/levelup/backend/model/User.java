package com.levelup.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 150)
    private String name;

    @Column(name = "fecha_nac", nullable = false)
    private LocalDateTime fechaNac;

    @Column(name = "tipo", nullable = false)
    private Integer tipo = 0; // 0=CLIENTE, 1=VENDEDOR, 2=ADMIN

    public User() {
    }

    public User(Long id, String email, String password, String name, LocalDateTime fechaNac, Integer tipo) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.fechaNac = fechaNac;
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(LocalDateTime fechaNac) {
        this.fechaNac = fechaNac;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    // Método de compatibilidad para código legacy
    public Boolean getIsAdmin() {
        return tipo == 2;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.tipo = isAdmin ? 2 : 0;
    }

    // Métodos helper para facilitar el uso
    public String getTipoDescripcion() {
        switch (tipo) {
            case 0: return "CLIENTE";
            case 1: return "VENDEDOR";
            case 2: return "ADMIN";
            default: return "DESCONOCIDO";
        }
    }

    public boolean isCliente() {
        return tipo == 0;
    }

    public boolean isVendedor() {
        return tipo == 1;
    }

    public boolean isAdministrador() {
        return tipo == 2;
    }
}
