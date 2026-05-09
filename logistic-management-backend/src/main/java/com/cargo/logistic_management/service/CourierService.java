package com.cargo.logistic_management.service;

import com.cargo.logistic_management.entity.Shipment;
import com.cargo.logistic_management.entity.ShipmentStatus;
import com.cargo.logistic_management.exception.ResourceNotFoundException;
import com.cargo.logistic_management.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourierService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentTrackingService trackingService;

    public List<Shipment> getMyAssignedShipments(String courierEmail) {
        return shipmentRepository.findByCourier_Email(courierEmail);
    }

    @Transactional
    public Shipment updateShipmentStatus(Long shipmentId, String courierEmail, ShipmentStatus newStatus) {

        Shipment shipment = shipmentRepository.findByIdAndCourier_Email(shipmentId, courierEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Bu kargo size zimmetli değil veya bulunamadı!"));

        shipment.setStatus(newStatus);
        Shipment savedShipment = shipmentRepository.save(shipment);

        trackingService.createLog(savedShipment, newStatus, "Kargo kurye tarafından '" + newStatus + "' olarak güncellendi.");

        return savedShipment;
    }
}