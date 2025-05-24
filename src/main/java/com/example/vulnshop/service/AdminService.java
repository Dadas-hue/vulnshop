package com.example.vulnshop.service;

import java.util.List;
import java.util.Map;

/**
 * 后台管理服务接口
 * 提供后台管理相关的业务方法
 */
public interface AdminService {
    
    /**
     * 获取仪表盘统计数据
     * @return 统计数据DTO
     */
    DashboardStatsDTO getDashboardStats();
    
    /**
     * 获取所有用户
     * @return 用户列表
     */
    List<Map<String, Object>> getAllUsers();
    
    /**
     * 获取所有商品
     * @return 商品列表
     */
    List<Map<String, Object>> getAllProducts();
    
    /**
     * 获取所有订单
     * @return 订单列表
     */
    List<Map<String, Object>> getAllOrders();
    
    /**
     * 搜索用户
     * @param keyword 搜索关键词
     * @return 匹配的用户列表
     */
    List<Map<String, Object>> searchUsers(String keyword);
} 