package com.vulnshop;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);

    List<User> findByUsernameAndPassword(String username, String password);
} 