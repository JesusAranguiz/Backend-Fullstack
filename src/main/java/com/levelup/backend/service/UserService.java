package com.levelup.backend.service;

import com.levelup.backend.model.User;
import com.levelup.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository repo;

    public List<User> getAll() {
        return repo.findAll();
    }

    public User getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public User getByEmail(String email) {
        return repo.findByEmail(email).orElse(null);
    }

    public User create(User user) {
        return repo.save(user);
    }

    public User update(Long id, User user) {
        User existing = getById(id);
        if (existing == null)
            return null;
        if (user.getEmail() != null) existing.setEmail(user.getEmail());
        if (user.getName() != null) existing.setName(user.getName());
        if (user.getPassword() != null) existing.setPassword(user.getPassword());
        if (user.getFechaNac() != null) existing.setFechaNac(user.getFechaNac());
        if (user.getIsAdmin() != null) existing.setIsAdmin(user.getIsAdmin());
        if (user.getTipo() != null) existing.setTipo(user.getTipo());
        return repo.save(existing);
    }

    public User updateTipo(Long id, Integer tipo) {
        User existing = getById(id);
        if (existing == null)
            return null;
        existing.setTipo(tipo);
        return repo.save(existing);
    }

    public List<User> getByTipo(Integer tipo) {
        return repo.findAll().stream()
            .filter(user -> tipo.equals(user.getTipo()))
            .toList();
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    /**
     * Verifica si el ID corresponde al usuario actualmente autenticado
     */
    public boolean isCurrentUser(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        
        String currentEmail = auth.getName();
        User currentUser = getByEmail(currentEmail);
        
        return currentUser != null && currentUser.getId().equals(id);
    }
}