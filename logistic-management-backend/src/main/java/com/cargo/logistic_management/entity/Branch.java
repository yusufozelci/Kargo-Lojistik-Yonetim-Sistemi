package com.cargo.logistic_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "branches")
@SQLDelete(sql = "UPDATE branches SET is_deleted = true WHERE id = ?")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Branch extends BaseEntity {
    @Column(nullable = false)
    private String name;

    private String city;

    @Column(name = "is_transfer_center")
    private Boolean isTransferCenter = false;
}