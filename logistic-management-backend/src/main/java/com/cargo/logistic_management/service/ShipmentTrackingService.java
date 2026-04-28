package com.cargo.logistic_management.service;

import com.cargo.logistic_management.datatransferobject.ShipmentTrackingResponseDto;
import com.cargo.logistic_management.entity.Shipment;
import com.cargo.logistic_management.entity.ShipmentStatus;
import com.cargo.logistic_management.entity.ShipmentTracking;
import com.cargo.logistic_management.exception.ResourceNotFoundException;
import com.cargo.logistic_management.repository.ShipmentRepository;
import com.cargo.logistic_management.repository.ShipmentTrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipmentTrackingService {

    private final ShipmentTrackingRepository trackingRepository;
    private final ShipmentRepository shipmentRepository;

    public List<ShipmentTrackingResponseDto> getTrackingHistory(String trackingCode) {
        Shipment shipment = shipmentRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Kargo bulunamadı: " + trackingCode));

        return trackingRepository.findAllByShipmentIdOrderByCreatedAtDesc(shipment.getId())
                .stream()
                .map(log -> new ShipmentTrackingResponseDto(
                        log.getStatus(),
                        log.getDescription(),
                        log.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void createLog(Shipment shipment, ShipmentStatus status, String description) {
        ShipmentTracking log = new ShipmentTracking();
        log.setShipment(shipment);
        log.setStatus(status);
        log.setDescription(description);
        trackingRepository.save(log);
    }
}