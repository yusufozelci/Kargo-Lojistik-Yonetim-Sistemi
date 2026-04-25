package com.cargo.logistic_management.repository;

import com.cargo.logistic_management.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    List<Branch> findByCity(String city);
    Branch findByName(String name);
}
