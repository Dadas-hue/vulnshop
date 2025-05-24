package com.example.vulnshop.service;

import java.util.List;
import com.example.vulnshop.entity.Product;

public interface ProductService {
    List<Product> searchProducts(String name);
    Product getProductById(String id);
} 