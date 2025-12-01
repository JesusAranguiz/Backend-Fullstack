package com.levelup.backend.repository;

import com.levelup.backend.model.Blogs;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BlogsRepository extends JpaRepository<Blogs, Long> {
    // Métodos de búsqueda personalizados
    List<Blogs> findByCategory(String category);
    List<Blogs> findByTitleContainingIgnoreCaseOrExcerptContainingIgnoreCase(String title, String excerpt);
}
