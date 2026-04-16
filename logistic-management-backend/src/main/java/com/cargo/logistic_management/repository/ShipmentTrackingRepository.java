package com.cargo.logistic_management.repository;

import com.cargo.logistic_management.entity.ShipmentTracking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentTrackingRepository extends JpaRepository<ShipmentTracking, Long> {

}