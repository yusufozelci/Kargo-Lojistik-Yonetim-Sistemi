package com.cargo.logistic_management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;


@Entity
@Table(name = "shipment_tracking")
@SQLDelete(sql = "UPDATE shipment_tracking SET is_deleted = true WHERE id = ?")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = "shipment")
public class ShipmentTracking extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    @JsonIgnore
    private Shipment shipment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShipmentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Branch location;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

}