package com.cargo.logistic_management.repository;

import com.cargo.logistic_management.entity.ShipmentTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ShipmentTrackingRepository extends JpaRepository<ShipmentTracking, Long> {
    List<ShipmentTracking> findAllByShipmentIdOrderByCreatedAtDesc(Long shipmentId);
}