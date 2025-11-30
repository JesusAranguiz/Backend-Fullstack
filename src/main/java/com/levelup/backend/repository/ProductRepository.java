package com.levelup.backend.repository;

import com.levelup.backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Si necesitas búsquedas específicas, puedes agregar métodos aquí
}
