package com.vulnshop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class OrderController {
    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/orders")
    public String list(@RequestParam(required = false) String username, Model model) {
        String sql = "SELECT o.* FROM orders o JOIN users u ON o.user_id = u.id JOIN products p ON o.product_id = p.id";
        if (username != null && !username.isEmpty()) {
            sql += " WHERE u.username = '" + username + "'";
        }
        List<Order> orders = entityManager.createNativeQuery(sql, Order.class).getResultList();
        model.addAttribute("orders", orders);
        return "orders";
    }

    @GetMapping("/order/{id}")
    public String detail(@PathVariable String id, Model model) {
        String sql = "SELECT * FROM orders WHERE id = " + id;
        List<Order> orders = entityManager.createNativeQuery(sql, Order.class).getResultList();
        if (!orders.isEmpty()) {
            model.addAttribute("order", orders.get(0));
        }
        return "order_detail";
    }
} 