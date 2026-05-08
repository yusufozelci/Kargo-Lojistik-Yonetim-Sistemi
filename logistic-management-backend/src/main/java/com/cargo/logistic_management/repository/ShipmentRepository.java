package com.cargo.logistic_management.repository;

import com.cargo.logistic_management.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Optional<Shipment> findByTrackingCode(String trackingCode);

    List<Shipment> findAllBySenderPhoneOrReceiverPhone(String senderPhone, String receiverPhone);
}