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
        // Calcular el total basado en los items
        double total = 0.0;
        for (OrderItem item : order.getItems()) {
            Product product = productRepo.findById(item.getProduct().getId()).orElse(null);
            if (product != null) {
                item.setProduct(product);
                item.setUnitPrice(product.getPrice());
                item.setSubtotal(item.getQuantity() * product.getPrice());
                total += item.getSubtotal();
            }
        }
        order.setTotal(total);
        
        // Guardar la orden
        Order savedOrder = orderRepo.save(order);
        
        // Establecer la relación con los items
        for (OrderItem item : savedOrder.getItems()) {
            item.setOrder(savedOrder);
        }
        
        return orderRepo.save(savedOrder);
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
        
        existing.setStatus(status);
        return orderRepo.save(existing);
    }

    public void delete(Long id) {
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
