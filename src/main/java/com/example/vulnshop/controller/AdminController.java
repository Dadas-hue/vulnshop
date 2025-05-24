package com.example.vulnshop.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.vulnshop.entity.Product;
import com.example.vulnshop.entity.User;
import com.example.vulnshop.repository.ProductRepository;
import com.example.vulnshop.repository.UserRepository;
import com.example.vulnshop.service.AdminService;
import com.example.vulnshop.service.DashboardStatsDTO;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 后台管理控制器
 * 负责处理后台管理相关的请求
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    // 存储管理员会话令牌
    private static final Map<String, Long> adminSessions = new HashMap<>();
    
    // Cookie名称常量
    private static final String ADMIN_SESSION_COOKIE = "admin_session";
    
    // Cookie过期时间（秒）
    private static final int COOKIE_MAX_AGE = 3600; // 1小时
    
    // 上传文件保存路径
    private static final String UPLOAD_DIR = "src/main/resources/static/images/products/";
    
    /**
     * 后台登录页面
     * 
     * @return 登录页面视图
     */
    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }
    
    /**
     * 后台登录处理 - 改进版，使用参数化查询和Cookie
     * 
     * @param username 用户名
     * @param password 密码
     * @param response HTTP响应对象，用于设置Cookie
     * @param model 模型
     * @return 重定向到仪表盘或返回登录页面
     */
    @PostMapping("/login")
    public String login(@RequestParam String username, 
                        @RequestParam String password,
                        HttpServletResponse response,
                        Model model) {
        try {
            // 使用安全的参数化查询方法
            User adminUser = userRepository.findAdminByUsernameAndPassword(username, password);
            
            if (adminUser != null) {
                // 生成唯一会话令牌
                String sessionToken = UUID.randomUUID().toString();
                
                // 存储会话令牌与管理员ID的映射
                adminSessions.put(sessionToken, adminUser.getId());
                
                // 创建Cookie
                Cookie sessionCookie = new Cookie(ADMIN_SESSION_COOKIE, sessionToken);
                sessionCookie.setMaxAge(COOKIE_MAX_AGE);
                sessionCookie.setPath("/admin"); // 仅对/admin路径有效
                sessionCookie.setHttpOnly(true); // 防止JavaScript访问
                
                // 添加Cookie到响应
                response.addCookie(sessionCookie);
                
                return "redirect:/admin/dashboard";
            } else {
                model.addAttribute("error", "用户名或密码错误");
                return "admin/login";
            }
        } catch (Exception e) {
            log.error("登录异常: {}", e.getMessage(), e);
            model.addAttribute("error", "登录异常: " + e.getMessage());
            return "admin/login";
        }
    }
    
    /**
     * 检查管理员身份验证
     * 
     * @param request HTTP请求对象，用于获取Cookie
     * @return 管理员用户ID，如果未认证则返回null
     */
    private Long getAuthenticatedAdminId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ADMIN_SESSION_COOKIE.equals(cookie.getName())) {
                    return adminSessions.get(cookie.getValue());
                }
            }
        }
        return null;
    }
    
    /**
     * 后台仪表盘
     * 
     * @param request HTTP请求对象
     * @param model 模型
     * @return 仪表盘页面视图或重定向到登录页面
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, Model model) {
        // 检查管理员登录状态
        Long adminId = getAuthenticatedAdminId(request);
        if (adminId == null) {
            return "redirect:/admin/login";
        }
        
        try {
            // 获取管理员信息
            User adminUser = userRepository.findById(adminId).orElse(null);
            if (adminUser == null) {
                return "redirect:/admin/login";
            }
            
            // 通过服务层获取统计数据
            DashboardStatsDTO stats = adminService.getDashboardStats();
            
            // 添加基础统计数据到模型
            model.addAttribute("userCount", stats.getUserCount());
            model.addAttribute("productCount", stats.getProductCount());
            model.addAttribute("orderCount", stats.getOrderCount());
            
            // 添加最近用户和订单信息
            model.addAttribute("recentUsers", stats.getRecentUsers());
            model.addAttribute("recentOrders", stats.getRecentOrders());
            
            // 添加登录管理员信息
            model.addAttribute("adminUsername", adminUser.getUsername());
            
            // 添加系统信息
            Map<String, String> systemInfo = new HashMap<>();
            systemInfo.put("javaVersion", System.getProperty("java.version"));
            systemInfo.put("osName", System.getProperty("os.name"));
            systemInfo.put("osVersion", System.getProperty("os.version"));
            model.addAttribute("systemInfo", systemInfo);
            
            return "admin/dashboard";
        } catch (DataAccessException e) {
            log.error("获取仪表盘数据失败: {}", e.getMessage(), e);
            addDefaultStats(model);
            model.addAttribute("error", "数据库访问异常: " + e.getMessage());
            return "admin/dashboard";
        } catch (Exception e) {
            log.error("仪表盘异常: {}", e.getMessage(), e);
            addDefaultStats(model);
            model.addAttribute("error", "系统处理异常: " + e.getMessage());
            return "admin/dashboard";
        }
    }
    
    /**
     * 添加默认统计数据到模型
     * @param model 视图模型
     */
    private void addDefaultStats(Model model) {
        model.addAttribute("userCount", 0);
        model.addAttribute("productCount", 0);
        model.addAttribute("orderCount", 0);
        model.addAttribute("recentUsers", Collections.emptyList());
        model.addAttribute("recentOrders", Collections.emptyList());
    }
    
    /**
     * 用户管理
     * 
     * @param request HTTP请求对象
     * @param model 模型
     * @return 用户列表页面视图或重定向到登录页面
     */
    @GetMapping("/users")
    public String userList(HttpServletRequest request, Model model) {
        if (getAuthenticatedAdminId(request) == null) {
            return "redirect:/admin/login";
        }
        
        try {
            List<Map<String, Object>> users = adminService.getAllUsers();
            model.addAttribute("users", users);
            return "admin/users";
        } catch (Exception e) {
            log.error("获取用户列表失败: {}", e.getMessage(), e);
            model.addAttribute("users", Collections.emptyList());
            model.addAttribute("error", "获取用户数据失败");
            return "admin/users";
        }
    }
    
    /**
     * 商品管理
     * 
     * @param request HTTP请求对象
     * @param model 模型
     * @return 商品列表页面视图或重定向到登录页面
     */
    @GetMapping("/products")
    public String productList(HttpServletRequest request, Model model) {
        if (getAuthenticatedAdminId(request) == null) {
            return "redirect:/admin/login";
        }
        
        try {
            List<Map<String, Object>> products = adminService.getAllProducts();
            model.addAttribute("products", products);
            return "admin/products";
        } catch (Exception e) {
            log.error("获取商品列表失败: {}", e.getMessage(), e);
            model.addAttribute("products", Collections.emptyList());
            model.addAttribute("error", "获取商品数据失败");
            return "admin/products";
        }
    }
    
    /**
     * 显示添加商品表单
     * 
     * @param request HTTP请求对象
     * @param model 模型
     * @return 添加商品表单页面或重定向到登录页面
     */
    @GetMapping("/products/add")
    public String showAddProductForm(HttpServletRequest request, Model model) {
        Long adminId = getAuthenticatedAdminId(request);
        if (adminId == null) {
            return "redirect:/admin/login";
        }
        
        try {
            // 获取管理员信息
            User adminUser = userRepository.findById(adminId).orElse(null);
            if (adminUser == null) {
                return "redirect:/admin/login";
            }
            
            // 添加登录管理员信息
            model.addAttribute("adminUsername", adminUser.getUsername());
            
            return "admin/add-product";
        } catch (Exception e) {
            log.error("显示添加商品表单失败: {}", e.getMessage(), e);
            model.addAttribute("error", "系统错误，请稍后重试");
            return "admin/add-product";
        }
    }
    
    /**
     * 处理添加商品请求
     * 
     * @param name 商品名称
     * @param price 价格
     * @param stock 库存
     * @param description 描述
     * @param categoryId 分类ID
     * @param imageFile 上传的图片文件
     * @param imageUrl 图片URL
     * @param request HTTP请求对象
     * @param model 模型
     * @return 重定向到商品列表或返回添加商品表单
     */
    @PostMapping("/products/add")
    public String addProduct(
            @RequestParam String name,
            @RequestParam BigDecimal price,
            @RequestParam Integer stock,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) MultipartFile imageFile,
            @RequestParam(required = false) String imageUrl,
            HttpServletRequest request,
            Model model) {
        
        Long adminId = getAuthenticatedAdminId(request);
        if (adminId == null) {
            return "redirect:/admin/login";
        }
        
        try {
            // 获取管理员信息
            User adminUser = userRepository.findById(adminId).orElse(null);
            if (adminUser == null) {
                return "redirect:/admin/login";
            }
            
            // 添加登录管理员信息
            model.addAttribute("adminUsername", adminUser.getUsername());
            
            // 创建商品对象 - 存在XSS漏洞，不对name和description进行过滤
            Product product = new Product();
            product.setName(name); // XSS漏洞：直接存储用户输入，不进行HTML转义
            product.setPrice(price);
            product.setStock(stock);
            product.setDescription(description); // XSS漏洞：直接存储用户输入，不进行HTML转义
            product.setCategoryId(categoryId);
            
            // 处理商品图片
            String finalImageUrl = null;
            
            // 如果上传了文件 - 存在文件上传漏洞，不检查文件类型
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    // 确保上传目录存在
                    File uploadDir = new File(UPLOAD_DIR);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }
                    
                    // 生成文件名 - 文件上传漏洞：保留原始文件名和扩展名
                    String originalFilename = imageFile.getOriginalFilename();
                    
                    // 文件上传漏洞：不验证文件类型，允许上传任何扩展名的文件
                    // 包括可执行文件如.jsp, .php等
                    
                    // 保存文件 - 直接使用原始文件名
                    Path filePath = Paths.get(UPLOAD_DIR + originalFilename);
                    Files.write(filePath, imageFile.getBytes());
                    
                    // 设置图片URL
                    finalImageUrl = "/images/products/" + originalFilename;
                    
                    log.info("文件已上传: {}", originalFilename);
                } catch (IOException e) {
                    log.error("保存商品图片失败: {}", e.getMessage(), e);
                    model.addAttribute("error", "保存商品图片失败，请重试");
                    return "admin/add-product";
                }
            }
            // 如果提供了图片URL - URL注入漏洞，不验证URL
            else if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                finalImageUrl = imageUrl.trim(); // 不验证URL，可能导致SSRF漏洞
            }
            
            // 设置图片URL
            product.setImageUrl(finalImageUrl);
            
            // 保存商品到数据库
            productRepository.save(product);
            
            // 添加成功消息
            model.addAttribute("success", "商品添加成功");
            
            // 重定向到商品列表页面
            return "redirect:/admin/products";
        } catch (Exception e) {
            log.error("添加商品失败: {}", e.getMessage(), e);
            model.addAttribute("error", "添加商品失败: " + e.getMessage());
            return "admin/add-product";
        }
    }
    
    /**
     * 订单管理
     * 
     * @param request HTTP请求对象
     * @param model 模型
     * @return 订单列表页面视图或重定向到登录页面
     */
    @GetMapping("/orders")
    public String orderList(HttpServletRequest request, Model model) {
        if (getAuthenticatedAdminId(request) == null) {
            return "redirect:/admin/login";
        }
        
        try {
            List<Map<String, Object>> orders = adminService.getAllOrders();
            model.addAttribute("orders", orders);
            return "admin/orders";
        } catch (Exception e) {
            log.error("获取订单列表失败: {}", e.getMessage(), e);
            model.addAttribute("orders", Collections.emptyList());
            model.addAttribute("error", "获取订单数据失败");
            return "admin/orders";
        }
    }
    
    /**
     * 用户搜索 - 改进版，使用参数化查询
     * 
     * @param keyword 搜索关键词
     * @param request HTTP请求对象
     * @return 用户列表
     */
    @GetMapping("/searchUser")
    @ResponseBody
    public List<Map<String, Object>> searchUser(@RequestParam String keyword, HttpServletRequest request) {
        if (getAuthenticatedAdminId(request) == null) {
            return List.of(Map.of("error", "未登录"));
        }
        
        try {
            // 使用安全的参数化查询
            return adminService.searchUsers(keyword);
        } catch (Exception e) {
            log.error("搜索用户失败: {}", e.getMessage(), e);
            return List.of(Map.of("error", "搜索失败: " + e.getMessage()));
        }
    }
    
    /**
     * 商品搜索 - 存在SQL注入漏洞
     * 
     * @param keyword 搜索关键词
     * @param request HTTP请求对象
     * @return 商品列表
     */
    @GetMapping("/searchProduct")
    @ResponseBody
    public List<Map<String, Object>> searchProduct(@RequestParam String keyword, HttpServletRequest request) {
        if (getAuthenticatedAdminId(request) == null) {
            return List.of(Map.of("error", "未登录"));
        }
        
        try {
            // SQL注入漏洞：直接拼接SQL语句
            String sql = "SELECT * FROM products WHERE name LIKE '%" + keyword + "%' OR description LIKE '%" + keyword + "%'";
            log.info("执行SQL查询: {}", sql);
            
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            log.error("搜索商品失败: {}", e.getMessage(), e);
            return List.of(Map.of("error", "搜索失败: " + e.getMessage()));
        }
    }
    
    /**
     * 系统信息 - 存在命令执行漏洞
     * 
     * @param cmd 要执行的命令
     * @param request HTTP请求对象
     * @return 命令执行结果
     */
    @GetMapping("/system")
    @ResponseBody
    public Map<String, Object> systemInfo(@RequestParam(required = false) String cmd, HttpServletRequest request) {
        Long adminId = getAuthenticatedAdminId(request);
        if (adminId == null) {
            return Map.of("error", "未登录");
        }
        
        Map<String, Object> result = new HashMap<>();
        
        // 基本系统信息
        result.put("javaVersion", System.getProperty("java.version"));
        result.put("osName", System.getProperty("os.name"));
        result.put("osVersion", System.getProperty("os.version"));
        result.put("userDir", System.getProperty("user.dir"));
        
        // 命令执行漏洞：直接执行用户提供的命令
        if (cmd != null && !cmd.trim().isEmpty()) {
            try {
                log.info("执行系统命令: {}", cmd);
                Process process = Runtime.getRuntime().exec(cmd);
                
                // 读取命令输出
                java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()));
                
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                
                // 等待命令执行完成
                int exitCode = process.waitFor();
                
                result.put("commandOutput", output.toString());
                result.put("exitCode", exitCode);
            } catch (Exception e) {
                log.error("执行命令失败: {}", e.getMessage(), e);
                result.put("error", "执行命令失败: " + e.getMessage());
            }
        }
        
        return result;
    }
    
    /**
     * 退出登录
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @return 重定向到登录页面
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // 清除会话令牌
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ADMIN_SESSION_COOKIE.equals(cookie.getName())) {
                    adminSessions.remove(cookie.getValue());
                    
                    // 清除Cookie
                    Cookie sessionCookie = new Cookie(ADMIN_SESSION_COOKIE, "");
                    sessionCookie.setMaxAge(0);
                    sessionCookie.setPath("/admin");
                    response.addCookie(sessionCookie);
                    break;
                }
            }
        }
        
        return "redirect:/admin/login";
    }
} 