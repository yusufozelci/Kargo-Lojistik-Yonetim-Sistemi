package com.cargo.logistic_management.entity;

import jakarta.persistence.*;
import lombok.Data;

@jakarta.persistence.Entity
@Table(name = "customers")
@Data
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    private String phone;

    @Column(name = "customer_type")
    private String customerType;
}