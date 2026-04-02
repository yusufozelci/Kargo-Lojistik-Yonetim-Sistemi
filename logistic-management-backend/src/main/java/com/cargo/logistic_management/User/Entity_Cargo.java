package com.cargo.logistic_management.User;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@jakarta.persistence.Entity
@Table(name = "users")
@Data
public class Entity_Cargo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name")
    private String fullName;

    private Boolean status = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}