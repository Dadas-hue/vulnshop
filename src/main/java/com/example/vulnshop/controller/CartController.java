package com.example.vulnshop.controller;

import com.example.vulnshop.entity.CartItem;
import com.example.vulnshop.entity.Product;
import com.example.vulnshop.entity.User;
import com.example.vulnshop.repository.CartItemRepository;
import com.example.vulnshop.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CartController {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login?redirect=/cart";
        }
        
        model.addAttribute("cartItems", cartItemRepository.findByUser(user));
        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId,
                          @RequestParam Integer quantity,
                          HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login?redirect=/product/" + productId;
        }

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return "redirect:/products";
        }

        CartItem existingItem = cartItemRepository.findByUserAndProductId(user, productId);
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }

        return "redirect:/cart";
    }
} 