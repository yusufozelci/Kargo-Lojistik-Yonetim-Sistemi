package com.cargo.logistic_management.controller;

import com.cargo.logistic_management.entity.User;
import com.cargo.logistic_management.datatransferobject.UserRegisterDto;
import com.cargo.logistic_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("UserRegisterDto") UserRegisterDto dto) {
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        userRepository.save(user);
        return "redirect:/login";
    }
}