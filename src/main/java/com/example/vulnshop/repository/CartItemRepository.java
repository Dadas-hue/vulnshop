package com.example.vulnshop.repository;

import com.example.vulnshop.entity.CartItem;
import com.example.vulnshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
    CartItem findByUserAndProductId(User user, Long productId);
} 