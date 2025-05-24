package com.example.vulnshop.config;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 全局异常处理器
 * 负责处理整个应用程序的异常
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 处理数据库访问异常
     * 
     * @param e 数据库访问异常
     * @param request HTTP请求
     * @param model 视图模型
     * @return 错误页面视图
     */
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleDataAccessException(DataAccessException e, HttpServletRequest request, Model model) {
        log.error("数据库访问异常: {}, URL: {}", e.getMessage(), request.getRequestURL(), e);
        
        model.addAttribute("timestamp", LocalDateTime.now().format(FORMATTER));
        model.addAttribute("error", "数据库访问错误");
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("message", "系统无法访问数据库或查询执行失败");
        model.addAttribute("path", request.getRequestURI());
        
        // 在开发环境中可以显示详细错误，生产环境中应隐藏
        // model.addAttribute("trace", e.toString());
        
        return "error/500";
    }

    /**
     * 处理SQL异常
     * 
     * @param e SQL异常
     * @param request HTTP请求
     * @param model 视图模型
     * @return 错误页面视图
     */
    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleSQLException(SQLException e, HttpServletRequest request, Model model) {
        log.error("SQL异常: {}, URL: {}", e.getMessage(), request.getRequestURL(), e);
        
        model.addAttribute("timestamp", LocalDateTime.now().format(FORMATTER));
        model.addAttribute("error", "数据库错误");
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("message", "数据库操作失败");
        model.addAttribute("path", request.getRequestURI());
        
        return "error/500";
    }

    /**
     * 处理所有其他未捕获的异常
     * 
     * @param e 异常
     * @param request HTTP请求
     * @param model 视图模型
     * @return 错误页面视图
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralException(Exception e, HttpServletRequest request, Model model) {
        log.error("未处理的异常: {}, URL: {}", e.getMessage(), request.getRequestURL(), e);
        
        model.addAttribute("timestamp", LocalDateTime.now().format(FORMATTER));
        model.addAttribute("error", "系统错误");
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("message", "处理请求时发生未知错误");
        model.addAttribute("path", request.getRequestURI());
        
        return "error";
    }
} 