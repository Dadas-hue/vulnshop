package com.example.vulnshop.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘统计数据DTO
 * 用于封装管理后台统计信息
 */
public class DashboardStatsDTO {
    
    /**
     * 用户数量
     */
    private long userCount;
    
    /**
     * 商品数量
     */
    private long productCount;
    
    /**
     * 订单数量
     */
    private long orderCount;
    
    /**
     * 最近登录的用户列表
     */
    private List<Map<String, Object>> recentUsers = Collections.emptyList();
    
    /**
     * 最新订单列表
     */
    private List<Map<String, Object>> recentOrders = Collections.emptyList();
    
    public DashboardStatsDTO() {
    }
    
    public DashboardStatsDTO(long userCount, long productCount, long orderCount, 
                           List<Map<String, Object>> recentUsers, 
                           List<Map<String, Object>> recentOrders) {
        this.userCount = userCount;
        this.productCount = productCount;
        this.orderCount = orderCount;
        this.recentUsers = recentUsers;
        this.recentOrders = recentOrders;
    }
    
    // Getters and Setters
    public long getUserCount() {
        return userCount;
    }
    
    public void setUserCount(long userCount) {
        this.userCount = userCount;
    }
    
    public long getProductCount() {
        return productCount;
    }
    
    public void setProductCount(long productCount) {
        this.productCount = productCount;
    }
    
    public long getOrderCount() {
        return orderCount;
    }
    
    public void setOrderCount(long orderCount) {
        this.orderCount = orderCount;
    }
    
    public List<Map<String, Object>> getRecentUsers() {
        return recentUsers;
    }
    
    public void setRecentUsers(List<Map<String, Object>> recentUsers) {
        this.recentUsers = recentUsers;
    }
    
    public List<Map<String, Object>> getRecentOrders() {
        return recentOrders;
    }
    
    public void setRecentOrders(List<Map<String, Object>> recentOrders) {
        this.recentOrders = recentOrders;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * 使用默认值创建实例
     * @return 包含默认值的统计对象
     */
    public static DashboardStatsDTO createDefault() {
        return new DashboardStatsDTO(0, 0, 0, Collections.emptyList(), Collections.emptyList());
    }
    
    public static class Builder {
        private long userCount;
        private long productCount;
        private long orderCount;
        private List<Map<String, Object>> recentUsers = Collections.emptyList();
        private List<Map<String, Object>> recentOrders = Collections.emptyList();
        
        public Builder userCount(long userCount) {
            this.userCount = userCount;
            return this;
        }
        
        public Builder productCount(long productCount) {
            this.productCount = productCount;
            return this;
        }
        
        public Builder orderCount(long orderCount) {
            this.orderCount = orderCount;
            return this;
        }
        
        public Builder recentUsers(List<Map<String, Object>> recentUsers) {
            this.recentUsers = recentUsers;
            return this;
        }
        
        public Builder recentOrders(List<Map<String, Object>> recentOrders) {
            this.recentOrders = recentOrders;
            return this;
        }
        
        public DashboardStatsDTO build() {
            return new DashboardStatsDTO(userCount, productCount, orderCount, recentUsers, recentOrders);
        }
    }
} 