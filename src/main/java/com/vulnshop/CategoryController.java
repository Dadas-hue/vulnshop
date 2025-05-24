package com.vulnshop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;

    // Vulnerable to SQL injection
    @GetMapping("/search")
    public List<Category> searchCategories(@RequestParam String name) {
        return categoryRepository.findByNameContaining(name);
    }

    // Vulnerable to SQL injection
    @GetMapping("/search/description")
    public List<Category> searchByDescription(@RequestParam String description) {
        return categoryRepository.findByDescriptionContaining(description);
    }

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return categoryRepository.save(category);
    }

    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        return categoryRepository.save(category);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryRepository.deleteById(id);
    }
} 