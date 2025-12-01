package com.levelup.backend.controller;

import com.levelup.backend.model.Blogs;
import com.levelup.backend.service.BlogsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/blogs")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Blogs", description = "Gestión de artículos del blog")
@SecurityRequirement(name = "bearerAuth")
public class BlogsController {
    @Autowired
    private BlogsService service;

    @Operation(summary = "Listar todos los blogs", description = "Disponible para USER y ADMIN. Puede filtrar por categoría y búsqueda")
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<Blogs> list(@RequestParam(required = false) String category,
                            @RequestParam(required = false) String q) {
        if (category != null || q != null) {
            return service.filterByCategoryAndSearch(category, q);
        }
        return service.getAll();
    }

    @Operation(summary = "Obtener todas las categorías", description = "Disponible para USER y ADMIN")
    @GetMapping("/categories")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<String> getCategories() {
        return service.getAllCategories();
    }

    @Operation(summary = "Obtener blog por ID", description = "Disponible para USER y ADMIN")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Blogs get(@PathVariable Long id) {
        return service.getById(id);
    }

    @Operation(summary = "Crear nuevo blog", description = "Solo disponible para ADMIN")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Blogs create(@RequestBody Blogs blog) {
        return service.create(blog);
    }

    @Operation(summary = "Actualizar blog existente", description = "Solo disponible para ADMIN")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Blogs update(@PathVariable Long id, @RequestBody Blogs blog) {
        return service.update(id, blog);
    }

    @Operation(summary = "Eliminar blog", description = "Solo disponible para ADMIN")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
