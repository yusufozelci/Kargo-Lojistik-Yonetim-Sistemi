package com.cargo.logistic_management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "roles")
@SQLDelete(sql = "UPDATE roles SET is_deleted = true WHERE id = ?")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity {
    @Column(name = "role_name", unique = true, nullable = false)
    private String roleName;
}