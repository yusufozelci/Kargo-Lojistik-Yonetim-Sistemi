package com.cargo.logistic_management.controller;

import com.cargo.logistic_management.entity.Shipment;
import com.cargo.logistic_management.entity.ShipmentStatus;
import com.cargo.logistic_management.service.CourierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courier")
@RequiredArgsConstructor
public class CourierController {

    private final CourierService courierService;

    @GetMapping("/my-shipments")
    public ResponseEntity<List<Shipment>> getMyShipments(Authentication authentication) {
        String courierEmail = authentication.getName();
        return ResponseEntity.ok(courierService.getMyAssignedShipments(courierEmail));
    }

    @PatchMapping("/shipments/{id}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable Long id,
            @RequestParam ShipmentStatus status,
            Authentication authentication) {

        String courierEmail = authentication.getName();
        courierService.updateShipmentStatus(id, courierEmail, status);

        return ResponseEntity.ok("Kargo durumu başarıyla '" + status + "' olarak güncellendi.");
    }
}