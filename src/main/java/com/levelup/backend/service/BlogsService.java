package com.levelup.backend.service;

import com.levelup.backend.model.Blogs;
import com.levelup.backend.repository.BlogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogsService {
    @Autowired
    private BlogsRepository repo;

    public List<Blogs> getAll() {
        return repo.findAll();
    }

    public Blogs getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public List<Blogs> getByCategory(String category) {
        return repo.findByCategory(category);
    }

    public List<Blogs> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return repo.findAll();
        }
        return repo.findByTitleContainingIgnoreCaseOrExcerptContainingIgnoreCase(query, query);
    }

    public List<Blogs> filterByCategoryAndSearch(String category, String query) {
        List<Blogs> blogs = repo.findAll();
        
        return blogs.stream()
            .filter(blog -> {
                boolean matchesCategory = category == null || 
                                         category.equals("Todas") || 
                                         category.isEmpty() || 
                                         category.equalsIgnoreCase(blog.getCategory());
                
                boolean matchesQuery = query == null || 
                                      query.trim().isEmpty() || 
                                      blog.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                                      blog.getExcerpt().toLowerCase().contains(query.toLowerCase());
                
                return matchesCategory && matchesQuery;
            })
            .collect(Collectors.toList());
    }

    public List<String> getAllCategories() {
        return repo.findAll().stream()
            .map(Blogs::getCategory)
            .distinct()
            .collect(Collectors.toList());
    }

    public Blogs create(Blogs blog) {
        return repo.save(blog);
    }

    public Blogs update(Long id, Blogs blog) {
        Blogs existing = getById(id);
        if (existing == null)
            return null;
        
        existing.setTitle(blog.getTitle());
        existing.setExcerpt(blog.getExcerpt());
        existing.setContent(blog.getContent());
        existing.setCategory(blog.getCategory());
        existing.setImage(blog.getImage());
        existing.setAuthor(blog.getAuthor());
        existing.setPublishedDate(blog.getPublishedDate());
        
        return repo.save(existing);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}