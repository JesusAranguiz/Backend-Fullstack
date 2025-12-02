package com.levelup.backend.service;

import com.levelup.backend.model.Order;
import com.levelup.backend.model.OrderItem;
import com.levelup.backend.model.Product;
import com.levelup.backend.repository.OrderRepository;
import com.levelup.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private ProductRepository productRepo;

    public List<Order> getAll() {
        return orderRepo.findAll();
    }

    public Order getById(Long id) {
        return orderRepo.findById(id).orElse(null);
    }

    public List<Order> getByUserId(Long userId) {
        return orderRepo.findByUserId(userId);
    }

    public List<Order> getByStatus(String status) {
        return orderRepo.findByStatus(status);
    }

    @Transactional
    public Order create(Order order) {
        // Validar y descontar stock
        for (OrderItem item : order.getItems()) {
            Product product = productRepo.findById(item.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: ID " + item.getProduct().getId()));
            
            // Validar stock suficiente
            if (product.getStock() == null || product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + product.getName() + 
                    ". Disponible: " + (product.getStock() != null ? product.getStock() : 0) + 
                    ", Solicitado: " + item.getQuantity());
            }
            
            // Descontar stock
            product.setStock(product.getStock() - item.getQuantity());
            productRepo.save(product);
            
            // Configurar el item
            item.setProduct(product);
            item.setUnitPrice(product.getPrice());
            item.setSubtotal(item.getQuantity() * product.getPrice());
            // Establecer la relación bidireccional ANTES de guardar
            item.setOrder(order);
        }
        
        // Calcular el total basado en los items
        double total = order.getItems().stream()
            .mapToDouble(OrderItem::getSubtotal)
            .sum();
        order.setTotal(total);
        
        // Guardar la orden (cascade guardará los items automáticamente)
        return orderRepo.save(order);
    }

    @Transactional
    public Order update(Long id, Order order) {
        Order existing = getById(id);
        if (existing == null)
            return null;
        
        existing.setStatus(order.getStatus());
        existing.setDeliveryAddress(order.getDeliveryAddress());
        existing.setPaymentMethod(order.getPaymentMethod());
        existing.setCustomerName(order.getCustomerName());
        existing.setCustomerEmail(order.getCustomerEmail());
        
        return orderRepo.save(existing);
    }

    @Transactional
    public Order updateStatus(Long id, String status) {
        Order existing = getById(id);
        if (existing == null)
            return null;
        
        String oldStatus = existing.getStatus();
        
        // Si se está cancelando una orden que no estaba cancelada, restaurar stock
        if ("CANCELADO".equals(status) && !"CANCELADO".equals(oldStatus)) {
            for (OrderItem item : existing.getItems()) {
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
                productRepo.save(product);
            }
        }
        
        existing.setStatus(status);
        return orderRepo.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        Order order = getById(id);
        if (order != null && !"CANCELADO".equals(order.getStatus())) {
            // Restaurar stock si la orden no estaba cancelada
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
                productRepo.save(product);
            }
        }
        orderRepo.deleteById(id);
    }

    // Métodos adicionales para estadísticas
    public Long countByStatus(String status) {
        return (long) orderRepo.findByStatus(status).size();
    }

    public Double getTotalRevenue() {
        return orderRepo.findAll().stream()
            .filter(order -> !"CANCELADO".equals(order.getStatus()))
            .mapToDouble(Order::getTotal)
            .sum();
    }
}
