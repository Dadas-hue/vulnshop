package com.vulnshop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ProductController {
    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/products")
    public String list(@RequestParam(required = false) String name, Model model) {
        String sql = "SELECT * FROM products";
        if (name != null && !name.isEmpty()) {
            sql += " WHERE name LIKE '%" + name + "%'";
        }
        List<Product> products = entityManager.createNativeQuery(sql, Product.class).getResultList();
        model.addAttribute("products", products);
        return "products";
    }

    @GetMapping("/product/{id}")
    public String detail(@PathVariable String id, Model model) {
        String sql = "SELECT * FROM products WHERE id = " + id;
        List<Product> products = entityManager.createNativeQuery(sql, Product.class).getResultList();
        if (!products.isEmpty()) {
            model.addAttribute("product", products.get(0));
        }
        return "product_detail";
    }
} 