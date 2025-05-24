package com.example.vulnshop.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 后台管理服务实现类
 */
@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = DashboardStatsDTO.builder().build();
        
        try {
            // 分别获取每个统计数据，一个错误不影响其他数据
            try {
                Number userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Number.class);
                stats.setUserCount(userCount != null ? userCount.longValue() : 0);
            } catch (Exception e) {
                log.error("获取用户数量失败", e);
                stats.setUserCount(0);
            }
            
            try {
                Number productCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM products", Number.class);
                stats.setProductCount(productCount != null ? productCount.longValue() : 0);
            } catch (Exception e) {
                log.error("获取商品数量失败", e);
                stats.setProductCount(0);
            }
            
            try {
                Number orderCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orders", Number.class);
                stats.setOrderCount(orderCount != null ? orderCount.longValue() : 0);
            } catch (Exception e) {
                log.error("获取订单数量失败", e);
                stats.setOrderCount(0);
            }
            
            // 获取最近登录的5个用户
            try {
                List<Map<String, Object>> recentUsers = jdbcTemplate.queryForList(
                    "SELECT id, username, email, last_login FROM users ORDER BY last_login DESC LIMIT 5");
                stats.setRecentUsers(recentUsers);
            } catch (Exception e) {
                log.error("获取最近用户登录信息失败", e);
                stats.setRecentUsers(Collections.emptyList());
            }
            
            // 获取最新的5个订单
            try {
                List<Map<String, Object>> recentOrders = jdbcTemplate.queryForList(
                    "SELECT o.id, o.order_date, o.total, u.username FROM orders o " +
                    "JOIN users u ON o.user_id = u.id ORDER BY o.order_date DESC LIMIT 5");
                stats.setRecentOrders(recentOrders);
            } catch (Exception e) {
                log.error("获取最新订单信息失败", e);
                stats.setRecentOrders(Collections.emptyList());
            }
            
            return stats;
        } catch (Exception e) {
            log.error("获取仪表盘统计数据时发生未知异常", e);
            return DashboardStatsDTO.createDefault();
        }
    }

    @Override
    public List<Map<String, Object>> getAllUsers() {
        try {
            return jdbcTemplate.queryForList("SELECT * FROM users");
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Map<String, Object>> getAllProducts() {
        try {
            return jdbcTemplate.queryForList("SELECT * FROM products");
        } catch (Exception e) {
            log.error("获取商品列表失败", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Map<String, Object>> getAllOrders() {
        try {
            return jdbcTemplate.queryForList(
                "SELECT o.*, u.username FROM orders o JOIN users u ON o.user_id = u.id");
        } catch (Exception e) {
            log.error("获取订单列表失败", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Map<String, Object>> searchUsers(String keyword) {
        try {
            // 使用参数化查询代替字符串拼接
            String sql = "SELECT * FROM users WHERE username LIKE ? OR email LIKE ?";
            String searchPattern = "%" + keyword + "%";
            return jdbcTemplate.queryForList(sql, searchPattern, searchPattern);
        } catch (Exception e) {
            log.error("搜索用户失败，关键词: {}", keyword, e);
            return Collections.emptyList();
        }
    }
} 