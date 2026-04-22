package com.cargo.logistic_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "vehicles")
@SQLDelete(sql = "UPDATE vehicles SET is_deleted = true WHERE id = ?")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Vehicle extends BaseEntity {
    @Column(name = "plate_number", unique = true)
    private String plateNumber;

    @Column(name = "vehicle_type")
    private String vehicleType;

    private Double capacity;
}