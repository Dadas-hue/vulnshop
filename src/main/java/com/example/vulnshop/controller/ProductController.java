package com.example.vulnshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.vulnshop.service.ProductService;

@Controller
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping("/")
    public String index() {
        return "redirect:/products";
    }
    
    @GetMapping("/products")
    public String listProducts(@RequestParam(required = false) String name, Model model) {
        // SQL注入点1: 商品搜索
        model.addAttribute("products", productService.searchProducts(name));
        return "products";
    }
    
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable String id, Model model) {
        // SQL注入点2: 商品详情
        model.addAttribute("product", productService.getProductById(id));
        return "product_detail";
    }
} 