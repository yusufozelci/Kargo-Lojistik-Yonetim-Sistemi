package com.cargo.logistic_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "shipments")
@SQLDelete(sql = "UPDATE shipments SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {"sender", "receiver", "originAddress", "destinationAddress", "courier"})
public class Shipment extends BaseEntity {

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
    private ShipmentStatus status = ShipmentStatus.PENDING;

    private Double weight;
    private Double distance;
    private Double totalPrice;
}