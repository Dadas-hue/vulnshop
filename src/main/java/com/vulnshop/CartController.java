package com.vulnshop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartRepository cartRepository;

    // Vulnerable to SQL injection
    @GetMapping("/user/{userId}")
    public List<Cart> getCartByUserId(@PathVariable String userId) {
        return cartRepository.findByUserId(userId);
    }

    // Vulnerable to SQL injection
    @GetMapping("/product/{productId}")
    public List<Cart> getCartByProductId(@PathVariable String productId) {
        return cartRepository.findByProductId(productId);
    }

    @PostMapping
    public Cart addToCart(@RequestBody Cart cart) {
        return cartRepository.save(cart);
    }

    @DeleteMapping("/{id}")
    public void removeFromCart(@PathVariable Long id) {
        cartRepository.deleteById(id);
    }
} 