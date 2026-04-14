package com.cargo.logistic_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tracking_code", unique = true, nullable = false, length = 20)
    private String trackingCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Customer sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Customer receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_address_id")
    private Address originAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_address_id")
    private Address destinationAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id")
    private User courier;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ShipmentStatus status;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "distance")
    private Double distance;

    @Column(name = "total_price")
    private Double totalPrice;
}