package com.vulnshop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    // 存在SQL注入漏洞 - 直接字符串拼接
    @Query(value = "SELECT * FROM cart_items WHERE user_id = " + "#{user_id}", nativeQuery = true)
    List<Cart> findByUserId(@Param("user_id") String userId);

    // 存在SQL注入漏洞 - 直接字符串拼接
    @Query(value = "SELECT * FROM cart_items WHERE product_id = " + "#{product_id}", nativeQuery = true)
    List<Cart> findByProductId(@Param("product_id") String productId);
} 