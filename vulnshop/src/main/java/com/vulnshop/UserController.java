package com.vulnshop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping({"/", "/login"})
    public String loginPage() {
        return "login";
    }

    // 漏洞登录接口（存在SQL注入风险）
    @PostMapping("/user/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        String sql = "SELECT * FROM users WHERE username='" + username + "' AND password='" + password + "'";
        List<User> result = entityManager.createNativeQuery(sql, User.class).getResultList();
        if (!result.isEmpty()) {
            model.addAttribute("msg", "登录成功，欢迎：" + username);
        } else {
            model.addAttribute("msg", "用户名或密码错误");
        }
        return "login";
    }
} 