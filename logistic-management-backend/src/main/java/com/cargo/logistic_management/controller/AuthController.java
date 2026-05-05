package com.cargo.logistic_management.controller;

import com.cargo.logistic_management.datatransferobject.UserRegisterDto;
import com.cargo.logistic_management.entity.Role;
import com.cargo.logistic_management.entity.User;
import com.cargo.logistic_management.repository.RoleRepository;
import com.cargo.logistic_management.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("UserRegisterDto") UserRegisterDto dto,
            BindingResult bindingResult
    ) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            bindingResult.rejectValue("email", "email.exists", "Bu e-posta adresi zaten kayıtlı.");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        Role customerRole = roleRepository.findByRoleName("CUSTOMER");

        if (customerRole == null) {
            customerRole = new Role();
            customerRole.setRoleName("CUSTOMER");
            customerRole = roleRepository.save(customerRole);
        }

        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRole(customerRole);
        user.setStatus(true);

        userRepository.save(user);

        return "redirect:/login?registered=true";
    }
}