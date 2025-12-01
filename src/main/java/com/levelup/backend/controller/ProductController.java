package com.levelup.backend.controller;

import com.levelup.backend.model.Product;
import com.levelup.backend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Productos", description = "Gestión de productos de videojuegos")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {
    @Autowired
    private ProductService service;

    @Operation(summary = "Listar todos los productos", description = "Disponible para USER y ADMIN")
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<Product> list() {
        return service.getAll();
    }

    @Operation(summary = "Obtener producto por ID", description = "Disponible para USER y ADMIN")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Product get(@PathVariable Long id) {
        return service.getById(id);
    }

    @Operation(summary = "Crear nuevo producto", description = "Solo disponible para ADMIN")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Product create(@RequestBody Product product) {
        return service.create(product);
    }

    @Operation(summary = "Actualizar producto existente", description = "Solo disponible para ADMIN")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Product update(@PathVariable Long id, @RequestBody Product product) {
        return service.update(id, product);
    }

    @Operation(summary = "Eliminar producto", description = "Solo disponible para ADMIN")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}