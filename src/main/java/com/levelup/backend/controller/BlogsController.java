package com.levelup.backend.controller;

import com.levelup.backend.model.Blogs;
import com.levelup.backend.service.BlogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/blogs")
@CrossOrigin(origins = "http://localhost:3000") // permite React en localhost:3000
public class BlogsController {
    @Autowired
    private BlogsService service;

    @GetMapping
    public List<Blogs> list(@RequestParam(required = false) String category,
                            @RequestParam(required = false) String q) {
        if (category != null || q != null) {
            return service.filterByCategoryAndSearch(category, q);
        }
        return service.getAll();
    }

    @GetMapping("/categories")
    public List<String> getCategories() {
        return service.getAllCategories();
    }

    @GetMapping("/{id}")
    public Blogs get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public Blogs create(@RequestBody Blogs blog) {
        return service.create(blog);
    }

    @PutMapping("/{id}")
    public Blogs update(@PathVariable Long id, @RequestBody Blogs blog) {
        return service.update(id, blog);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
