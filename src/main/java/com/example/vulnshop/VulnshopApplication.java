package com.example.vulnshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication  // 默认会扫描当前包及其子包
public class VulnshopApplication {
    public static void main(String[] args) {
        SpringApplication.run(VulnshopApplication.class, args);
    }
} 