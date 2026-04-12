package com.cargo.logistic_management.Cargo;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@jakarta.persistence.Entity
@Table(name = "shipments")
@Data
public class Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tracking_code", unique = true, nullable = false)
    private String trackingCode;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private com.cargo.logistic_management.Customer.Entity sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private com.cargo.logistic_management.Customer.Entity receiver;

    private Double weight;

    private BigDecimal price;
}