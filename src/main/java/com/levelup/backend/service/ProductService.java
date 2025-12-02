package com.levelup.backend.service;

import com.levelup.backend.model.Product;
import com.levelup.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository repo;

    public List<Product> getAll() {
        return repo.findAll();
    }

    public Product getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Product create(Product product) {
        return repo.save(product);
    }

    public Product update(Long id, Product product) {
        Product existing = getById(id);
        if (existing == null)
            return null;
        if (product.getName() != null) existing.setName(product.getName());
        if (product.getCategory() != null) existing.setCategory(product.getCategory());
        if (product.getPrice() != null) existing.setPrice(product.getPrice());
        if (product.getDescription() != null) existing.setDescription(product.getDescription());
        if (product.getOferta() != null) existing.setOferta(product.getOferta());
        if (product.getImage() != null) existing.setImage(product.getImage());
        if (product.getStock() != null) existing.setStock(product.getStock());
        return repo.save(existing);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
