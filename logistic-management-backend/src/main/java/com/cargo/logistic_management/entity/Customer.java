package com.cargo.logistic_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "customers")
@SQLDelete(sql = "UPDATE customers SET is_deleted = true WHERE id = ?")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Customer extends BaseEntity {



    @Column(name = "full_name", nullable = false)
    private String fullName;

    private String phone;

    @Column(name = "customer_type")
    private String customerType;
}