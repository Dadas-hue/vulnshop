package com.example.vulnshop.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 系统初始化检查
 * 应用启动时验证系统基础组件
 */
@Component
public class SystemInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SystemInitializer.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        log.info("系统初始化检查开始...");
        
        try {
            // 检查数据库连接
            jdbcTemplate.execute("SELECT 1");
            log.info("数据库连接正常");
            
            // 检查users表结构
            checkTableStructure("users", new String[]{"id", "username", "password", "email", "is_admin", "last_login"});
            
            // 检查products表结构
            checkTableStructure("products", new String[]{"id", "name", "description", "price", "image_url"});
            
            // 检查orders表结构
            checkTableStructure("orders", new String[]{"id", "user_id", "order_date", "total"});
            
            log.info("系统初始化检查完成，一切正常");
        } catch (Exception e) {
            log.error("系统初始化检查失败: {}", e.getMessage(), e);
            log.warn("系统可能无法正常工作，请检查数据库配置和表结构");
        }
    }
    
    /**
     * 检查表结构
     * 
     * @param tableName 表名
     * @param expectedColumns 期望的列名
     */
    private void checkTableStructure(String tableName, String[] expectedColumns) {
        try {
            // 获取表的所有列名
            var columns = jdbcTemplate.queryForList(
                    "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ?",
                    String.class, tableName);
            
            log.info("表 {} 的列: {}", tableName, columns);
            
            // 检查是否存在所有期望的列
            for (String column : expectedColumns) {
                if (!columns.contains(column)) {
                    log.warn("表 {} 缺少列: {}", tableName, column);
                }
            }
            
            log.info("表 {} 结构检查完成", tableName);
        } catch (Exception e) {
            log.error("检查表 {} 结构时出错: {}", tableName, e.getMessage(), e);
        }
    }
} 