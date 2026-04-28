package com.cargo.logistic_management.repository;

import com.cargo.logistic_management.entity.RouteShipment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RouteShipmentRepository extends JpaRepository<RouteShipment, Long> {
    List<RouteShipment> findByRouteId(Long routeId);
}