package com.vulnshop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Vulnerable to SQL injection - direct string concatenation
    @Query(value = "SELECT * FROM categories WHERE name LIKE " + "'%#{name}%'", nativeQuery = true)
    List<Category> findByNameContaining(@Param("name") String name);

    // Vulnerable to SQL injection - direct string concatenation
    @Query(value = "SELECT * FROM categories WHERE description LIKE " + "'%#{description}%'", nativeQuery = true)
    List<Category> findByDescriptionContaining(@Param("description") String description);
} 