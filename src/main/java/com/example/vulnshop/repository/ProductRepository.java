package com.example.vulnshop.repository;

import com.example.vulnshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
} 