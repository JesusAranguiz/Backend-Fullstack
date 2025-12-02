package com.levelup.backend.controller;

import com.levelup.backend.model.Order;
import com.levelup.backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Órdenes", description = "Gestión de órdenes de compra")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {
    @Autowired
    private OrderService service;

    @Operation(summary = "Listar órdenes", description = "Lista todas las órdenes, con filtros opcionales por usuario o estado. ADMIN ve todas, otros usuarios solo las suyas")
    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'VENDEDOR', 'ADMIN')")
    public List<Order> list(@RequestParam(required = false) Long userId,
                           @RequestParam(required = false) String status) {
        if (userId != null) {
            return service.getByUserId(userId);
        }
        if (status != null) {
            return service.getByStatus(status);
        }
        return service.getAll();
    }

    @Operation(summary = "Obtener orden por ID", description = "Obtiene los detalles de una orden específica")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'VENDEDOR', 'ADMIN')")
    public ResponseEntity<Order> get(@PathVariable Long id) {
        Order order = service.getById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Crear nueva orden", description = "Crea una orden y descuenta el stock automáticamente. Valida stock disponible")
    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'VENDEDOR', 'ADMIN')")
    public ResponseEntity<?> create(@RequestBody Order order) {
        try {
            Order created = service.create(order);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            // Capturar errores de stock insuficiente o producto no encontrado
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(summary = "Actualizar orden", description = "Actualiza los datos de una orden. Solo ADMIN o VENDEDOR")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDEDOR', 'ADMIN')")
    public ResponseEntity<Order> update(@PathVariable Long id, @RequestBody Order order) {
        Order updated = service.update(id, order);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Actualizar estado de orden", description = "Cambia el estado de una orden. Si se cancela, restaura el stock automáticamente")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('VENDEDOR', 'ADMIN')")
    public ResponseEntity<Order> updateStatus(@PathVariable Long id, 
                                              @RequestBody Map<String, String> body) {
        String status = body.get("status");
        Order updated = service.updateStatus(id, status);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Eliminar orden", description = "Elimina una orden y restaura el stock. Solo ADMIN")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Estadísticas de órdenes", description = "Obtiene estadísticas generales de órdenes. Solo ADMIN o VENDEDOR")
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('VENDEDOR', 'ADMIN')")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRevenue", service.getTotalRevenue());
        stats.put("pendingOrders", service.countByStatus("PENDIENTE"));
        stats.put("processingOrders", service.countByStatus("PROCESANDO"));
        stats.put("deliveredOrders", service.countByStatus("ENTREGADO"));
        return stats;
    }

    @Operation(summary = "Contar órdenes totales", description = "Obtiene el número total de órdenes. Solo ADMIN o VENDEDOR")
    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('VENDEDOR', 'ADMIN')")
    public ResponseEntity<Map<String, Long>> getCount() {
        Map<String, Long> response = new HashMap<>();
        response.put("count", service.countAll());
        return ResponseEntity.ok(response);
    }
}
