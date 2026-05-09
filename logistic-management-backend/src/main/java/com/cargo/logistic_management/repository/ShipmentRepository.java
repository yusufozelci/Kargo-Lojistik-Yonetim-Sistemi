package com.cargo.logistic_management.repository;

import com.cargo.logistic_management.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import com.cargo.logistic_management.entity.ShipmentStatus;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    Optional<Shipment> findByTrackingCode(String trackingCode);

    List<Shipment> findAllBySenderPhoneOrReceiverPhone(String senderPhone, String receiverPhone);
    List<Shipment> findByCourier_Email(String email);
    Optional<Shipment> findByIdAndCourier_Email(Long id, String email);
    long countByCourierId(Long courierId);
}