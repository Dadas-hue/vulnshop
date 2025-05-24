package com.example.vulnshop.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 自定义错误控制器
 * 处理所有错误请求，代替默认的WhitelabelErrorPage
 */
@Controller
public class CustomErrorController implements ErrorController {

    private static final Logger log = LoggerFactory.getLogger(CustomErrorController.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ErrorAttributes errorAttributes;

    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    /**
     * 处理/error路径的请求
     *
     * @param request HTTP请求
     * @param model 视图模型
     * @return 错误页面视图
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // 获取错误状态码
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        if (statusCode == null) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
        
        // 获取错误属性
        WebRequest webRequest = new ServletWebRequest(request);
        Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(
                webRequest, 
                org.springframework.boot.web.error.ErrorAttributeOptions.of(
                        org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE,
                        org.springframework.boot.web.error.ErrorAttributeOptions.Include.BINDING_ERRORS
                )
        );
        
        // 添加错误信息到模型
        model.addAttribute("timestamp", LocalDateTime.now().format(FORMATTER));
        model.addAttribute("status", statusCode);
        model.addAttribute("error", errorAttributes.get("error"));
        model.addAttribute("message", errorAttributes.get("message"));
        model.addAttribute("path", request.getRequestURI());
        
        log.error("错误处理: 状态码={}, 路径={}, 错误={}, 消息={}", 
                statusCode, request.getRequestURI(), errorAttributes.get("error"), errorAttributes.get("message"));
        
        // 根据状态码返回不同的错误页面
        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            return "error/404";
        } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
            return "error/403";
        } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            return "error/500";
        } else {
            return "error";
        }
    }
} 