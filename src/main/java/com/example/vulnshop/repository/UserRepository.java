package com.example.vulnshop.repository;

import com.example.vulnshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 故意使用字符串拼接来制造SQL注入漏洞
    @Query(value = "SELECT * FROM users WHERE username = ?1 AND password = ?2", nativeQuery = true)
    User findByUsernameAndPassword(String username, String password);
    
    // 安全的查询方法，使用命名参数
    @Query(value = "SELECT * FROM users WHERE username = :username AND password = :password AND is_admin = true", nativeQuery = true)
    User findAdminByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
    
    // 根据用户名查找用户
    User findByUsername(String username);
} 