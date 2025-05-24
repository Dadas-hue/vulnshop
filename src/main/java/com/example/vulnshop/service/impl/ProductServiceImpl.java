package com.example.vulnshop.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.vulnshop.service.ProductService;
import com.example.vulnshop.entity.Product;
import com.example.vulnshop.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Override
    public List<Product> searchProducts(String name) {
        List<Product> products = productRepository.findAll();
        
        // 如果正常从数据库中没有找到商品，创建一些示例商品
        if (products == null || products.isEmpty()) {
            products = createSampleProducts();
        }
        
        // 如果提供了搜索名称，进行过滤
        if (name != null && !name.trim().isEmpty()) {
            List<Product> filteredProducts = new ArrayList<>();
            for (Product product : products) {
                if (product.getName().toLowerCase().contains(name.toLowerCase())) {
                    filteredProducts.add(product);
                }
            }
            return filteredProducts;
        }
        
        return products;
    }

    @Override
    public Product getProductById(String id) {
        try {
            Long productId = Long.parseLong(id);
            Optional<Product> product = productRepository.findById(productId);
            
            if (product.isPresent()) {
                return product.get();
            } else {
                List<Product> sampleProducts = createSampleProducts();
                for (Product p : sampleProducts) {
                    if (p.getId().equals(productId)) {
                        return p;
                    }
                }
            }
        } catch (NumberFormatException e) {
            // 处理ID格式错误的情况
        }
        return null;
    }
    
    private List<Product> createSampleProducts() {
        List<Product> sampleProducts = new ArrayList<>();
        
        Product p1 = new Product();
        p1.setId(1L);
        p1.setName("智能手机");
        p1.setDescription("最新款高性能智能手机，搭载先进处理器和高清摄像头。");
        p1.setPrice(new BigDecimal("2999.00"));
        p1.setStock(100);
        p1.setImageUrl("/images/products/bottle.jpg");
        p1.setCategoryId(1);
        sampleProducts.add(p1);
        
        Product p2 = new Product();
        p2.setId(2L);
        p2.setName("笔记本电脑");
        p2.setDescription("轻薄高性能笔记本电脑，适合办公和娱乐。");
        p2.setPrice(new BigDecimal("5999.00"));
        p2.setStock(50);
        p2.setImageUrl("/images/products/pen.jpg");
        p2.setCategoryId(1);
        sampleProducts.add(p2);
        
        Product p3 = new Product();
        p3.setId(3L);
        p3.setName("智能手表");
        p3.setDescription("支持心率监测和各种运动模式的智能手表。");
        p3.setPrice(new BigDecimal("899.00"));
        p3.setStock(200);
        p3.setImageUrl("/images/products/gold.jpg");
        p3.setCategoryId(2);
        sampleProducts.add(p3);
        
        Product p4 = new Product();
        p4.setId(4L);
        p4.setName("无线耳机");
        p4.setDescription("高音质蓝牙无线耳机，支持降噪功能。");
        p4.setPrice(new BigDecimal("499.00"));
        p4.setStock(150);
        p4.setImageUrl("/images/products/boots.jpg");
        p4.setCategoryId(2);
        sampleProducts.add(p4);
        
        Product p5 = new Product();
        p5.setId(5L);
        p5.setName("平板电脑");
        p5.setDescription("大屏幕高清显示，适合学习和娱乐的平板电脑。");
        p5.setPrice(new BigDecimal("3299.00"));
        p5.setStock(80);
        p5.setImageUrl("/images/products/bottle.jpg");
        p5.setCategoryId(1);
        sampleProducts.add(p5);
        
        return sampleProducts;
    }
} 