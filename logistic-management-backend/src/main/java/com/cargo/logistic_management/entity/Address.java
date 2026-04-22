package com.cargo.logistic_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "addresses")
@SQLDelete(sql = "UPDATE addresses SET is_deleted = true WHERE id = ?")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Address extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private String title;
    private String city;
    private String district;

    @Column(name = "full_address", columnDefinition = "TEXT")
    private String fullAddress;
}