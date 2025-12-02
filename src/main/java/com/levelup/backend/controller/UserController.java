package com.levelup.backend.controller;

import com.levelup.backend.model.User;
import com.levelup.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    @Autowired
    private UserService service;

    @Operation(summary = "Listar todos los usuarios", description = "Solo disponible para ADMIN")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> list() {
        return service.getAll();
    }

    @Operation(summary = "Obtener usuario por ID", description = "ADMIN puede ver cualquier usuario, USER solo puede ver su propio perfil")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.isCurrentUser(#id)")
    public ResponseEntity<?> get(@PathVariable Long id) {
        User user = service.getById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Obtener perfil actual", description = "Obtiene el perfil del usuario autenticado con todos sus datos incluyendo tipo")
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('CLIENTE', 'VENDEDOR', 'ADMIN')")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = service.getByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Crear nuevo usuario", description = "Registro público (no requiere autenticación)")
    @PostMapping
    public User create(@RequestBody User user) {
        return service.create(user);
    }

    @Operation(summary = "Actualizar usuario", description = "ADMIN puede actualizar cualquier usuario, USER solo puede actualizar su propio perfil")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.isCurrentUser(#id)")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody User user) {
        User updated = service.update(id, user);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Eliminar usuario", description = "Solo disponible para ADMIN")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Actualizar tipo de usuario", description = "Solo disponible para ADMIN")
    @PatchMapping("/{id}/tipo")
    @PreAuthorize("hasRole('ADMIN')")
    public User updateTipo(@PathVariable Long id, @RequestBody java.util.Map<String, Integer> body) {
        Integer tipo = body.get("tipo");
        return service.updateTipo(id, tipo);
    }

    @Operation(summary = "Listar usuarios por tipo", description = "Solo disponible para ADMIN")
    @GetMapping("/by-tipo/{tipo}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getByTipo(@PathVariable Integer tipo) {
        return service.getByTipo(tipo);
    }
}
