package com.cargo.logistic_management.controller;

import com.cargo.logistic_management.datatransferobject.AuthenticationRequest;
import com.cargo.logistic_management.datatransferobject.AuthenticationResponse;
import com.cargo.logistic_management.datatransferobject.UserRegisterDto;
import com.cargo.logistic_management.datatransferobject.UserResponseDto;
import com.cargo.logistic_management.service.AuthenticationService;
import com.cargo.logistic_management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@SuppressWarnings("ALL")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRegisterDto userDto) {
        return new ResponseEntity<>(userService.createUser(userDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> hardDeleteUser(@PathVariable Long id) {
        userService.hardDeleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        userService.generateAndSendOtp(email);
        return ResponseEntity.ok("Şifre sıfırlama kodu e-posta adresinize başarıyla gönderildi.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword
    ) {
        try {
            userService.resetPassword(email, otp, newPassword, confirmPassword);
            return ResponseEntity.ok("Şifreniz başarıyla güncellendi.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}