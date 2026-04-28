package com.cargo.logistic_management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "route_shipments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class RouteShipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;
}