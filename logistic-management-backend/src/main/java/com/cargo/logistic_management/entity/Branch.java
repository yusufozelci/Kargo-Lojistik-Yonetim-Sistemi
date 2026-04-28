package com.cargo.logistic_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "branches")
@SQLDelete(sql = "UPDATE branches SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Branch extends BaseEntity {

    @NotBlank(message = "Şube ismi boş bırakılamaz")
    @Column(nullable = false)
    private String name;

    @Column(name = "is_transfer_center")
    private Boolean isTransferCenter = false;

    @OneToOne
    @JoinColumn(name = "address_id")
    private Address address;
}