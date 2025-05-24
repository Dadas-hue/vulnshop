package com.example.vulnshop.controller;

import com.example.vulnshop.entity.User;
import com.example.vulnshop.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                       @RequestParam String password,
                       @RequestParam(required = false) String redirect,
                       HttpSession session,
                       Model model) {
        User user = userRepository.findByUsernameAndPassword(username, password);
        
        if (user != null) {
            session.setAttribute("user", user);
            return redirect != null && !redirect.isEmpty() ? "redirect:" + redirect : "redirect:/products";
        }
        
        model.addAttribute("error", "用户名或密码错误");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
} 